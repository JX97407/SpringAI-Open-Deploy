package io.github.SpringAI.dto;

/**
 * @Description 聊天历史消息返回对象
 * @Author 刘争伟
 * @Date 2026/8/17 下午5:11
 **/
public record ChatMessageResponse (
        String speaker,
        String content
){
}
