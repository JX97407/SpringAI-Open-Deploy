package io.github.SpringAI.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * @Description AI对话业务层
 * @Author 刘争伟
 * @Date 2026/8/12 下午12:03
 **/
@Service
public class ChatService {

    private final ChatClient chatClient;

    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String reply(String question) {
        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }
}
