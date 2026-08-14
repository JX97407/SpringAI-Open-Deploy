package io.github.SpringAI.service;

import io.github.SpringAI.dto.ChatResponse;
import io.github.SpringAI.exception.AIChatException;
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
    private final String systemPrompt;

    public ChatService(ChatClient chatClient,
                       @Value("${spring.ai.ollama.chat.model}") String model,
                       @Value("${app.ai.system-prompt}") String systemPrompt) {

        this.chatClient = chatClient;
        this.model = model;
        this.systemPrompt = systemPrompt;
    }

    public ChatResponse reply(String question,String systemPrompt) {

        long startTime = System.currentTimeMillis();

        log.info("AI chat started , model={}, question={}",model,question);

        try {
            String answer = chatClient.prompt()
                    .system(systemPrompt)
                    .user(question)
                    .call()
                    .content();

            long durationMs = System.currentTimeMillis() - startTime;

            log.info("AI chat finished, model={}, durationMs={},answerLength={}", model, durationMs, answer.length());

            return new ChatResponse(question, answer, model, durationMs);
        }catch (Exception e){
            long durationMs = System.currentTimeMillis() - startTime;

            log.error("AI chat failed, model={}, durationMs={}, question={}", model, durationMs, question, e);

            throw new AIChatException("AI模型调用失败，请稍后重试",e);
        }
    }

    private String getFinalSystemPrompt(String requestSystemPrompt){
        if (requestSystemPrompt == null || requestSystemPrompt.isBlank()){
            return systemPrompt;
        }
        return requestSystemPrompt;
    }
}
