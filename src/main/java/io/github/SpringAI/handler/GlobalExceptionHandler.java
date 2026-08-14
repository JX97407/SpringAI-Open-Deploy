package io.github.SpringAI.handler;

import io.github.SpringAI.exception.AIChatException;
import io.github.SpringAI.vo.ReturnVO;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
    public ReturnVO<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        String message = e.getBindingResult()
                .getFieldError()
                .getDefaultMessage();
        return ReturnVO.fail(message);
    }

    @ExceptionHandler(Exception.class)
    public ReturnVO<Void> handleException(Exception e){
        return ReturnVO.fail(500, "服务器异常");
    }

    @ExceptionHandler(AIChatException.class)
    public ReturnVO<Void> handleAIChatException(AIChatException e){
        return ReturnVO.fail(500, e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ReturnVO<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
        return ReturnVO.fail(400, "请求体格式错误，请检查JSON格式；role可选值为TEACHER、INTERVIEWER、CODE_REVIEWER");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ReturnVO<Void> handleIllegalArgumentException(IllegalArgumentException e){
        return ReturnVO.fail(400, e.getMessage());
    }
}
