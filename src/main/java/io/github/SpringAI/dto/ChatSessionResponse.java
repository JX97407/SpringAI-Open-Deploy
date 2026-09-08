/**
 * 类名:ChatSessionResponse
 * 创建人:lzw    创建时间:2026/9/5
 */

package io.github.SpringAI.dto;

import io.github.SpringAI.entity.ChatSession;

import java.time.LocalDateTime;

/**
 * 〈功能简述〉聊天会话返回对象
 * 〈功能详细描述〉用于向前端返回会话列表所需的信息，
 *              避免直接暴露ChatSession JPA 实体
 * @author lzw
 */
public record ChatSessionResponse(
        String sessionId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    /**
     * 将 ChatSession 实体转换为接口返回对象
     */

    public static ChatSessionResponse from(ChatSession session){
        return new ChatSessionResponse(
                session.getSessionId(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }
}
