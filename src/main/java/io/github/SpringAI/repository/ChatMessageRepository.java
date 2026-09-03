package io.github.SpringAI.repository;

import io.github.SpringAI.entity.ChatMessage;
import io.github.SpringAI.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 〈功能简述〉聊天消息的数据访问层
 * @author lzw
 */

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatSession_SessionIdOrderByCreatedAtAscIdAsc(String sessionId); // 按会话ID 查询全部聊天信息，并按创建时间、消息ID 正序排列

    long countByChatSession(ChatSession chatSession); //统计当前会话有多少条消息

    List<ChatMessage> findTop2ByChatSessionOrderByCreatedAtAscIdAsc(ChatSession chatSession); // 查询指定会话中最早的两条聊天消息，用于按完整回答轮次清理旧消息

    void deleteByChatSession_SessionId(String sessionId); //删除指定会话的全部消息
}
