/**
 * 类名:ChatMemoryServiceTest
 * 创建人:lzw    创建时间:2026/9/8
 */

package io.github.SpringAI.memory;

import io.github.SpringAI.config.AIProperties;
import io.github.SpringAI.entity.ChatMessage;
import io.github.SpringAI.entity.ChatSession;
import io.github.SpringAI.entity.User;
import io.github.SpringAI.exception.ChatSessionConflictException;
import io.github.SpringAI.repository.ChatMessageRepository;
import io.github.SpringAI.repository.ChatSessionRepository;
import io.github.SpringAI.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    void shouldCreateNewSessionAndSaveCompleteConversation(){
        User user = new User("lzw");
        ChatSession savedSession = new ChatSession(
                "chat-002",
                user
        );

        //当前用户还没有这个会话
        when(chatSessionRepository.findByUser_IdAndSessionId(
                1L,
                "chat-002"
        )).thenReturn(Optional.empty());

        //用户存在，而且sessionId没有被其他用户占用
        when(userRepository.existsById(1L))
                .thenReturn(true);

        when(chatSessionRepository.findBySessionId("chat-002"))
                .thenReturn(Optional.empty());

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(chatSessionRepository.save(any(ChatSession.class)))
                .thenReturn(savedSession);

        when(chatMessageRepository.countByChatSession(savedSession))
                .thenReturn(2L);

        chatMemoryService.addConversation(
                1L,
                "chat-002",
                "什么是事务？",
                "事务是一组不可分割的数据库操作。"
        );

        ArgumentCaptor<ChatSession> sessionCaptor =
                ArgumentCaptor.forClass(ChatSession.class);

        verify(chatSessionRepository).save(sessionCaptor.capture());

        ChatSession createdSession = sessionCaptor.getValue();

        assertThat(createdSession.getSessionId())
                .isEqualTo("chat-002");

        assertThat(createdSession.getUser())
                .isSameAs(user);

        ArgumentCaptor<ChatMessage> messageCaptor =
                ArgumentCaptor.forClass(ChatMessage.class);

        verify(chatMessageRepository,times(2))
                .save(messageCaptor.capture());

        List<ChatMessage> savedMessages = messageCaptor.getAllValues();

        assertThat(savedMessages)
                .extracting(ChatMessage::getSpeaker)
                .containsExactly("user","assistant");

        assertThat(savedMessages)
                .extracting(ChatMessage::getContent)
                .containsExactly(
                        "什么是事务？",
                        "事务是一组不可分割的数据库操作。"
                );
    }

    @Test
    void shouldDeleteOldestPairWhenMessageLimitExceeded(){
        User user = new User("lzw");
        ChatSession session = new ChatSession("chat-001",user);

        ChatMessage oldestQuestion = new ChatMessage(
                session,"user","最早的问题"
        );

        ChatMessage oldestAnswer = new ChatMessage(
                session,"assistant","最早的回答"
        );

        List<ChatMessage> oldestPair = List.of(
                oldestQuestion,
                oldestAnswer
        );

        when(chatSessionRepository.findByUser_IdAndSessionId(
                1L,"chat-001"
        )).thenReturn(Optional.of(session));

        //首次检查超限：删除两条后，再次检查已达到上限
        when(chatMessageRepository.countByChatSession(session))
                .thenReturn(22L,20L);

        when(chatMessageRepository
                .findTop2ByChatSessionOrderByCreatedAtAscIdAsc(session))
                .thenReturn(oldestPair);

        chatMemoryService.addConversation(
                1L,
                "chat-001",
                "新的问题",
                "新的回答"
        );

        //本轮问题和回答均保存
        verify(chatMessageRepository,times(2))
                .save(any(ChatMessage.class));

        //删除的必须是最早的一问一答，并且只删除一次
        verify(chatMessageRepository,times(1))
                .deleteAll(oldestPair);

        verify(chatMessageRepository,times(1))
                .findTop2ByChatSessionOrderByCreatedAtAscIdAsc(session);

        verify(chatMessageRepository,times(2))
                .countByChatSession(session);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true,false}) //分别传入 true 和 false, 同一个方法执行两次
    void shouldClearHistoryOnlyForOwnedSession(boolean owned){
        User user = new User("lzw");
        ChatSession session = new ChatSession("chat-001",user);

        //只有属于当前用户的会话，才会查询到结果
        when(chatSessionRepository.findByUser_IdAndSessionId(
                1L,"chat-001"
        )).thenReturn(
                owned ? Optional.of(session) : Optional.empty()
        );

        //注意现有方法的参数顺序：sessionId在前，userId在后
        chatMemoryService.clearHistory("chat-001",1L);

        if (owned){
            InOrder order = inOrder(
                    chatMessageRepository,
                    chatSessionRepository
            );

            //消息引用会话，必须先删除消息，再删除会话
            order.verify(chatMessageRepository)
                    .deleteByChatSession_SessionId("chat-001");

            order.verify(chatSessionRepository)
                    .delete(session);
        }else {
            //未找到自己的会话时，不允许删除任何消息或会话
            verifyNoInteractions(chatMessageRepository);

            verify(chatSessionRepository,never())
                    .delete(any(ChatSession.class));
        }
    }
}
