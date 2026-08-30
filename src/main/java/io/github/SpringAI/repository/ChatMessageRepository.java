package io.github.SpringAI.repository;

import io.github.SpringAI.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatSession_SessionIdOrderByCreatedAtAsc(String sessionId);
}
