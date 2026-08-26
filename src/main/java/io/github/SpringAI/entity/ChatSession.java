/**
 * 类名:ChatSession
 * 创建人:lzw    创建时间:2026/8/25
 */

package io.github.SpringAI.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 〈功能简述〉聊天会话实体，对应数据库中的 chat_session 表
 * @author lzw
 */
@Getter
@Entity
@Table(
        name = "chat_session",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_chat_session_session_id",
                    columnNames = "session_id"
            )
}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatSession {

    @Id // 声明主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // @GeneratedValue:使用MySQL自增主键
    private Long id;

    @Column(name = "session_id", nullable = false, length = 64) // @Column：该字段不允许为null
    private String sessionId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ChatSession(String sessionId){
        this.sessionId = sessionId;
    }

    @PrePersist //首次插入数据库前自动执行
    private void beforeInsert(){
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate //更新数据库记录之前自动执行
    private void beforeUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
