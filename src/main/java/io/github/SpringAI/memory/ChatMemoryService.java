package io.github.SpringAI.memory;

import io.github.SpringAI.config.AIProperties;
import io.github.SpringAI.dto.ChatMessageResponse;
import io.github.SpringAI.entity.ChatMessage;
import io.github.SpringAI.entity.ChatSession;
import io.github.SpringAI.repository.ChatMessageRepository;
import io.github.SpringAI.repository.ChatSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description 保存聊天记录
 * @Author 刘争伟
 * @Date 2026/8/17 上午10:29
 **/
@Service
public class ChatMemoryService {

    private static final String USER_SPEAKER = "user";
    private static final String ASSISTANT_SPEAKER = "assistant";

    private final int maxMessages;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ChatMemoryService(
            ChatSessionRepository chatSessionRepository,
            ChatMessageRepository chatMessageRepository,
            AIProperties aiProperties){

        this.chatSessionRepository = chatSessionRepository;

        this.chatMessageRepository = chatMessageRepository;

        int configuredMaxMessages = aiProperties.memory().maxMessages();

        if (configuredMaxMessages <= 0){
            throw new IllegalArgumentException("app.ai.memory.max-messages 必须大于0");
        }

        if (configuredMaxMessages %2 != 0){
            throw new IllegalArgumentException("app.ai.memory.max-messages 必须是偶数");
        }

        this.maxMessages = configuredMaxMessages;
    }

    @Transactional(readOnly = true) // 表示这个事务只查询、不修改数据库，有助于表达方法意图
    public List<ConversationMessage> getHistory(String sessionId){
        if (sessionId == null || sessionId.isBlank()){
            return List.of();
        }

        return chatMessageRepository
                .findByChatSession_SessionIdOrderByCreatedAtAscIdAsc(sessionId)
                .stream()
                .map(message -> new ConversationMessage(
                        message.getSpeaker(),
                        message.getContent()
                )).toList();
    }

    @Transactional // 表示方法中的数据库操作属于同一个事务
    public void addMessage(
            String sessionId,
            ConversationMessage message
    ){
        if (sessionId == null || sessionId.isBlank()){
            return;
        }

        ChatSession chatSession = getOrCreateSession(sessionId);

        chatMessageRepository.save(
                new ChatMessage(
                        chatSession,
                        message.speaker(),
                        message.content()
                )
        );

        removeOldestMessage(chatSession);
    }

    @Transactional
    public void addConversation(
            String sessionId,
            String question,
            String answer
    ){
        if (sessionId == null || sessionId.isBlank()){
            return;
        }

        ChatSession chatSession = getOrCreateSession(sessionId);

        chatMessageRepository.save(
                new ChatMessage(
                        chatSession,
                        USER_SPEAKER,
                        question
                )
        );

        chatMessageRepository.save(
                new ChatMessage(
                        chatSession,
                        ASSISTANT_SPEAKER,
                        answer
                )
        );

        removeOldestMessage(chatSession);
    }

    @Transactional(readOnly = true)
    public String buildContext(String sessionId){
        return getHistory(sessionId)
                .stream()
                .map(message ->
                        message.speaker() + ": " + message.content()
                ).collect(Collectors.joining("\n"));
    }

    @Transactional
    public void clearHistory(String sessionId){
        if (sessionId == null || sessionId.isBlank()){
            return;
        }

        // 先删除消息，再删除会话，避免外键约束冲突
        chatMessageRepository.deleteByChatSession_SessionId(sessionId);

        chatSessionRepository
                .findBySessionId(sessionId)
                .ifPresent(chatSessionRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getStoredMessages(String sessionId){
        if (sessionId == null || sessionId.isBlank()){
            return List.of();
        }

        return chatMessageRepository
                .findByChatSession_SessionIdOrderByCreatedAtAscIdAsc(sessionId)
                .stream()
                .map(message -> new ChatMessageResponse(
                        message.getSpeaker(),
                        message.getContent(),
                        message.getCreatedAt()
                )).toList();
    }

    private ChatSession getOrCreateSession(String sessionId){
        return chatSessionRepository
                .findBySessionId(sessionId)
                .orElseGet(
                        () -> chatSessionRepository.save(
                                new ChatSession(sessionId)
                        )
                );
    }

    /**
     * 超过配置数量时，持续删除最早的消息
     */
    private void removeOldestMessage(ChatSession chatSession){
        while(chatMessageRepository.countByChatSession(chatSession) > maxMessages){

            List<ChatMessage> oldestMessages =
                    chatMessageRepository
                            .findTop2ByChatSessionOrderByCreatedAtAscIdAsc(
                                    chatSession
                            );

            if (oldestMessages.isEmpty()){
                return;
            }

            chatMessageRepository.deleteAll(oldestMessages);
        }
    }
}
