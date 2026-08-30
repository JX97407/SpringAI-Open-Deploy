/**
 * 类名:ChatSessionRepository
 * 创建人:lzw    创建时间:2026/8/30
 */

package io.github.SpringAI.repository;

import io.github.SpringAI.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 〈功能简述〉聊天会话的数据访问层
 * @author lzw
 */
public interface ChatSessionRepository  extends JpaRepository<ChatSession, Long> {
    Optional<ChatSession> findBySessionId(String sessionId);
}
