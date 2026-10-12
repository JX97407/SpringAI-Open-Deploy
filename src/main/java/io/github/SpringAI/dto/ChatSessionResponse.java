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
        String title,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    /**
     * 将 ChatSession 实体转换为接口返回对象
     */

    public static ChatSessionResponse from(ChatSession session){
        String title = session.getTitle();

        //兼容添加标题功能前已经存在的会话
        if (title == null || title.isBlank()){
            title = "新对话";
        }
        return new ChatSessionResponse(
                session.getSessionId(),
                title,
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }
}
