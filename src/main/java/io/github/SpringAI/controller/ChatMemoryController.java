package io.github.SpringAI.controller;
import io.github.SpringAI.dto.ChatMessageResponse;
import io.github.SpringAI.dto.ChatSessionResponse;
import io.github.SpringAI.memory.ChatMemoryService;
import io.github.SpringAI.vo.ReturnVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description 提供查询聊天记录和清空聊天记录
 * @Author 刘争伟
 * @Date 2026/8/17 下午5:35
 **/
@RestController
@RequestMapping("/ai/users/{userId}/sessions")
@Tag(name = "聊天记录报表")
public class ChatMemoryController {
    private final ChatMemoryService chatMemoryService;

    public ChatMemoryController(ChatMemoryService chatMemoryService){
        this.chatMemoryService = chatMemoryService;
    }

    @GetMapping("/{sessionId}/messages")
    @Operation(summary = "查询指定用户历史对话记录")
    public ReturnVO<List<ChatMessageResponse>>getHistory(
            @PathVariable("sessionId") String sessionId,
            @PathVariable("userId") Long userId
            ){
        return ReturnVO.success(chatMemoryService.getStoredMessages(userId,sessionId));
    }

    /**
     * 清空指定用户拥有的指定会话
     */
    @DeleteMapping("/{sessionId}")
    @Operation(summary = "删除指定用户的指定会话")
    public ReturnVO<Void> clearHistory(
            @PathVariable("sessionId") String sessionId,
            @PathVariable("userId") Long userId
    ){
        chatMemoryService.clearHistory(sessionId, userId);

        return ReturnVO.success(null);
    }


    /**
     * 查询指定用户拥有的全部聊天会话
     */
    @GetMapping
    @Operation(summary = "查询指定用户所有聊天会话")
    public ReturnVO<List<ChatSessionResponse>> getSessions(@PathVariable("userId") Long userId){
        return ReturnVO.success(chatMemoryService.getUserSessions(userId));
    }


}
