package io.github.SpringAI.controller;

import io.github.SpringAI.dto.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.SpringAI.service.ChatService;

/**
 * @Description AI对话控制层
 * @Author 刘争伟
 * @Date 2026/8/12 下午12:03
 **/
@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/ai")
    public ChatResponse chat(@RequestParam String q) {
        String answer = chatService.reply(q);
        return new ChatResponse(q,answer);
    }


}
