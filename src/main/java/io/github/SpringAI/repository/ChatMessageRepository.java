package io.github.SpringAI.repository;

import io.github.SpringAI.entity.ChatMessage;
import io.github.SpringAI.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 〈功能简述〉聊天消息的数据访问层
 * @author lzw
 */

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatSession_SessionIdOrderByCreatedAtAsc(String sessionId);

    long countByChatSession(ChatSession chatSession); //统计当前会话有多少条消息

    Optional<ChatMessage> findFirstByChatSessionOrderByCreatedAtAsc(ChatSession chatSession); //找到创建时间最早的一条消息

    void deleteByChatSession_SessionId(String sessionId); //删除指定会话的全部消息
}
