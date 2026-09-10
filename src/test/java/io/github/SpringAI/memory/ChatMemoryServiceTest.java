/**
 * 类名:ChatMemoryServiceTest
 * 创建人:lzw    创建时间:2026/9/8
 */

package io.github.SpringAI.memory;

import io.github.SpringAI.config.AIProperties;
import io.github.SpringAI.entity.ChatSession;
import io.github.SpringAI.entity.User;
import io.github.SpringAI.exception.ChatSessionConflictException;
import io.github.SpringAI.repository.ChatMessageRepository;
import io.github.SpringAI.repository.ChatSessionRepository;
import io.github.SpringAI.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * ChatMemoryService 单元测试
 * 用于验证用户、会话和聊天记忆之间的业务限制，
 * 不启动Spring Boot，也不连接真实 MySQL
 * @author lzw
 */
@ExtendWith(MockitoExtension.class)
class ChatMemoryServiceTest {

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AIProperties aiProperties;

    @Mock
    private AIProperties.Memory memoryProperties;

    private ChatMemoryService chatMemoryService;

    @BeforeEach
    void setUp(){
        when(aiProperties.memory())
                .thenReturn(memoryProperties);

        when(memoryProperties.maxMessages())
                .thenReturn(20);

        chatMemoryService = new ChatMemoryService(
                chatSessionRepository,
                chatMessageRepository,
                userRepository,
                aiProperties
        );
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist(){
        when(chatSessionRepository.findByUser_IdAndSessionId(
                1L,
                "chat-001"
        )).thenReturn(Optional.empty());

        when(userRepository.existsById(1L))
                .thenReturn(false);

        assertThatThrownBy(() ->
                chatMemoryService.addConversation(
                        1L,
                        "chat-001",
                        "测试问题",
                        "测试回答"
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("用户不存在");
    }

    @Test
    void shouldReuseOwnedSession(){
       User user = new User("lzw");
        ChatSession session = new ChatSession(
                "chat-001",
                user
        );

        when(chatSessionRepository.findByUser_IdAndSessionId(
                1L,
                "chat-001"
        )).thenReturn(Optional.of(session));

        when(chatMessageRepository.countByChatSession(session))
                .thenReturn(2L);

        chatMemoryService.addConversation(
                1L,
                "chat-001",
                "测试问题",
                "测试回答"
        );

        verify(chatSessionRepository)
                .findByUser_IdAndSessionId(1L,"chat-001");

        verify(chatMessageRepository,times(2))
                .save(any());

        verify(userRepository,never())
                .existsById(1L);

    }

    @Test
    void shouldThrowExceptionWhenSessionBelongsToAnotherUser(){
        User anotherUser = new User("another-user");
        ChatSession session = new ChatSession(
                "chat-001",
                anotherUser
        );

        when(chatSessionRepository.findByUser_IdAndSessionId(
                1L,
                "chat-001"
        )).thenReturn(Optional.empty());

        when(userRepository.existsById(1L))
                .thenReturn(true);

        when(chatSessionRepository.findBySessionId("chat-001"))
                .thenReturn(Optional.of(session));

        assertThatThrownBy(() ->
                chatMemoryService.addConversation(
                        1L,
                        "chat-001",
                        "测试问题",
                        "测试回答"
                )
        )
                .isInstanceOf(ChatSessionConflictException.class)
                .hasMessage("该sessionId已被其他用户使用，请更换sessionId");
    }
}
