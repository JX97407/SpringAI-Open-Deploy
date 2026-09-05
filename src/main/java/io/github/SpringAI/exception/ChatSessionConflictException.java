/**
 * 类名:ChatSessionConflictException
 * 创建人:lzw    创建时间:2026/9/5
 */

package io.github.SpringAI.exception;

/**
 * 〈功能简述〉聊天会话冲突异常
 * 〈功能详细描述〉当sessionId已经存在，但属于当前用户时抛出，
 *              用于区分会话问题和大模型调用问题
 * @author lzw
 */
public class ChatSessionConflictException extends RuntimeException{

    public ChatSessionConflictException(String message){
        super(message);
    }
}
