package io.github.SpringAI.service;

import io.github.SpringAI.config.AIProperties;
import io.github.SpringAI.dto.ChatResponse;
import io.github.SpringAI.enums.ChatRole;
import io.github.SpringAI.exception.AIChatException;
import io.github.SpringAI.memory.ChatMemoryService;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

/**
 * @Description AI对话业务层
 * @Author 刘争伟
 * @Date 2026/8/12 下午12:03
 **/
@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ChatClient chatClient;
    private final String model;
    private final ChatMemoryService chatMemoryService;
    private final AIProperties aiProperties;

    public ChatService(ChatClient chatClient,
                       @Value("${spring.ai.ollama.chat.model}") String model,
                       ChatMemoryService chatMemoryService,
                       AIProperties aiProperties) {

        this.chatClient = chatClient;
        this.model = model;
        this.chatMemoryService = chatMemoryService;
        this.aiProperties = aiProperties;
    }

    public ChatResponse reply(String question,  String requestRole, String sessionId) {

        long startTime = System.currentTimeMillis();

        String finalSystemPrompt = getFinalSystemPrompt(requestRole);
        String history = chatMemoryService.buildContext(sessionId);
        String userPrompt = buildUserPrompt(history,question);

        log.info("AI chat started , model={}, question={}",model,question);

        try {
            String answer = chatClient.prompt()
                    .system(finalSystemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();

            long durationMs = System.currentTimeMillis() - startTime;

            chatMemoryService.addConversation(
                    sessionId,
                    question,
                    answer
            );

            log.info("AI chat finished, model={}, durationMs={},answerLength={}", model, durationMs, answer.length());

            return new ChatResponse(question, answer, model, durationMs, sessionId);
        }catch (Exception e){
            long durationMs = System.currentTimeMillis() - startTime;

            log.error("AI chat failed, model={}, durationMs={}, question={}", model, durationMs, question, e);

            throw new AIChatException("AI模型调用失败，请稍后重试",e);
        }
    }
    private String getFinalSystemPrompt(String requestRole){
        ChatRole role = ChatRole.fromName(requestRole);

        if (role != null){
            return role.getRolePrompt();
        }

        return aiProperties.systemPrompt();
    }

    private String buildUserPrompt(
            String history,
            String question
    ){
        if (history == null || history.isBlank()){
            return question;
        }
        return """
                以下是之前的对话记录：
                %s
                
                当前问题：
                %s
                """.formatted(history,question);
    }
}


