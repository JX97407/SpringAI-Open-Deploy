package io.github.SpringAI.controller;

import io.github.SpringAI.dto.ChatResponse;
import io.github.SpringAI.vo.ChatQueryVO;
import io.github.SpringAI.vo.ReturnVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/ai/chat")
    public ReturnVO<ChatResponse> chat(@Valid @RequestBody ChatQueryVO chatQueryVO) {
        String question = chatQueryVO.getQuestion();
        String answer = chatService.reply(question);
        ChatResponse response = new ChatResponse(question, answer);
        return ReturnVO.success(response);
    }


}
