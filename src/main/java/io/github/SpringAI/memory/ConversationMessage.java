package io.github.SpringAI.memory;

/**
 * @Description 聊天记录类
 * @Author 刘争伟
 * @Date 2026/8/17 上午10:24
 **/
public record ConversationMessage(
        String speaker,
        String content
) {
}
