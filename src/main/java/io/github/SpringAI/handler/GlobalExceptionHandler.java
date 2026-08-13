package io.github.SpringAI.handler;

import io.github.SpringAI.exception.AIChatException;
import io.github.SpringAI.vo.ReturnVO;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Description 全局异常处理器
 * @Author 刘争伟
 * @Date 2026/8/13 上午11:46
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ReturnVO<Void> handleMethodArgumentNotVaildException(MethodArgumentNotValidException e){
        String message = e.getBindingResult()
                .getFieldError()
                .getDefaultMessage();
        return ReturnVO.fail(message);
    }

    @ExceptionHandler(Exception.class)
    public ReturnVO<Void> handleException(Exception e){
        return new  ReturnVO<>(500,"服务器内部异常",null);
    }

    @ExceptionHandler(AIChatException.class)
    public ReturnVO<Void> handleAIChatException(AIChatException e){
        return new ReturnVO<>(500,e.getMessage(),null);
    }
}
