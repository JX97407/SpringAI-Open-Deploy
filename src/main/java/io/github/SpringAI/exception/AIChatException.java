package io.github.SpringAI.exception;

/**
 * @Description AI对话异常
 * @Author 刘争伟
 * @Date 2026/8/13 下午3:47
 **/
public class AIChatException extends RuntimeException{

    public AIChatException(String message, Throwable cause){
        super(message,cause);
    }
}
