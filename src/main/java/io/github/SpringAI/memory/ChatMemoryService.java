package io.github.SpringAI.memory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @Description 保存聊天记录
 * @Author 刘争伟
 * @Date 2026/8/17 上午10:29
 **/
@Service
public class ChatMemoryService {

    private final Map<String, List<ConversationMessage>> sessions = new ConcurrentHashMap<>();
    private final int maxMessages;

    public List<ConversationMessage> getHistory(String sessionId){
        if (sessionId == null || sessionId.isBlank()){
            return List.of();
        }

        return List.copyOf(
                sessions.getOrDefault(sessionId,List.of())
        );
    }

    public void addMessage(
            String sessionId,
            ConversationMessage message
    ){
        if (sessionId == null || sessionId.isBlank()){
            return;
        }
        List<ConversationMessage> messages = sessions.computeIfAbsent(
                sessionId,
                key -> new CopyOnWriteArrayList<>()
        );

        messages.add(message);

        //只保留最近的消息，避免历史记录和提示词无限增长
        while(messages.size()>maxMessages){
            messages.remove(0);
        }
    }

    public String buildContext(String sessionId){
        return getHistory(sessionId)
                .stream()
                .map(message ->
                        message.speaker() + ": " + message.content()
                ).collect(Collectors.joining("\n"));
    }

    public void clearHistory(String sessionId){
        if (sessionId == null || sessionId.isBlank()){
            return;
        }

        sessions.remove(sessionId);
    }

    public ChatMemoryService(
            @Value("${app.ai.memory.max-messages:20}") int maxMessages
    ){
        this.maxMessages = maxMessages;
    }

}
