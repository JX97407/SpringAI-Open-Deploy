package io.github.SpringAI.controller;

import io.github.SpringAI.dto.ChatMessageResponse;
import io.github.SpringAI.memory.ChatMemoryService;
import io.github.SpringAI.vo.ReturnVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description 提供查询聊天记录和清空聊天记录
 * @Author 刘争伟
 * @Date 2026/8/17 下午5:35
 **/
@RestController
@RequestMapping("/ai/sessions")
public class ChatMemoryController {
    private final ChatMemoryService chatMemoryService;

    public ChatMemoryController(ChatMemoryService chatMemoryService){
        this.chatMemoryService = chatMemoryService;
    }

    @GetMapping("/{sessionId}/messages")
    public ReturnVO<List<ChatMessageResponse>>getHistory(
            @PathVariable String sessionId
    ){
        List<ChatMessageResponse> messages = chatMemoryService.getHistory(sessionId)
                .stream()
                .map(message -> new ChatMessageResponse(
                        message.speaker(),
                        message.content()
                )).toList();

        return ReturnVO.success(messages);
    }

    @DeleteMapping("/{sessionId}")
    public ReturnVO<Void> clearHistory(
            @PathVariable String sessionId
    ){
        chatMemoryService.clearHistory(sessionId);

        return ReturnVO.success(null);
    }




}
