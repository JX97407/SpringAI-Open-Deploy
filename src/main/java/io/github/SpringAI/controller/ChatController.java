package io.github.SpringAI.controller;

import io.github.SpringAI.dto.ChatResponse;
import io.github.SpringAI.vo.ChatQueryVO;
import io.github.SpringAI.vo.ReturnVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import io.github.SpringAI.service.ChatService;

/**
 * @Description AI对话控制层
 * @Author 刘争伟
 * @Date 2026/8/12 下午12:03
 **/
@RestController
@Tag(name = "AI对话入口")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/ai/chat")
    @Operation(summary = "AI大模型对话")
    public ReturnVO<ChatResponse> chat(@Valid @RequestBody ChatQueryVO chatQueryVO) {
        ChatResponse response = chatService.reply(
                chatQueryVO.getQuestion(),
                chatQueryVO.getRole(),
                chatQueryVO.getSessionId(),
                chatQueryVO.getUserId()
        );
        return ReturnVO.success(response);
    }


}
