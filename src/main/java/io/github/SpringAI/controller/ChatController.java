package io.github.SpringAI.controller;

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
    public String chat(@RequestParam String q) {
        return chatService.reply(q);
    }
}
