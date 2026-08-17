package io.github.SpringAI.memory;

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
        sessions.computeIfAbsent(
                sessionId,
                key -> new CopyOnWriteArrayList<>()
        ).add(message);
    }

    public String buildContext(String sessionId){
        return getHistory(sessionId)
                .stream()
                .map(message ->
                        message.speaker() + ": " + message.content()
                ).collect(Collectors.joining("\n"));
    }

}
