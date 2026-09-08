/**
 * 类名:ChatSessionRepository
 * 创建人:lzw    创建时间:2026/8/30
 */

package io.github.SpringAI.repository;

import io.github.SpringAI.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 〈功能简述〉聊天会话的数据访问层
 * @author lzw
 */
public interface ChatSessionRepository  extends JpaRepository<ChatSession, Long> {
    /**
     *根据用户ID 和会话ID 查询指定会话
     */
    Optional<ChatSession> findByUser_IdAndSessionId(Long userId, String sessionId);

    /**
     *根据会话ID 查询会话
     */
    Optional<ChatSession> findBySessionId(String sessionId);

    /**
     * 查询指定用户的全部会话，并按更新时间倒序排列
     */
    List<ChatSession> findAllByUser_IdOrderByUpdatedAtDesc(Long userId);
}
