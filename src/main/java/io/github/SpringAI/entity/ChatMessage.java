/**
 * 类名:ChatMessage
 * 创建人:lzw    创建时间:2026/8/26
 */

package io.github.SpringAI.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 〈功能简述〉聊天消息实体，对应chat_message 表
 * @author lzw
 */
@Getter
@Entity
@Table(
        name = "chat_message",
        indexes = {
                @Index(
                        name = "idx_chat_message_session_id",
                        columnList = "chat_session_id"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 多条消息可以属于同一个聊天会话
     */
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(
            name = "chat_session_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_chat_message_session")
    )
    private ChatSession chatSession;

    @Column(name = "speaker",nullable = false,length = 20)
    private String speaker;

    @Lob
    @Column(name = "content",nullable = false)
    private String content;

    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;

    public ChatMessage(
            ChatSession chatSession,
            String speaker,
            String content
    ){
        this.chatSession = chatSession;
        this.speaker = speaker;
        this.content = content;
    }

    @PrePersist
    private void beforeInsert(){
        this.createdAt = LocalDateTime.now();
    }
}
