package io.github.SpringAI.handler;

import io.github.SpringAI.exception.AIChatException;
import io.github.SpringAI.exception.ChatSessionConflictException;
import io.github.SpringAI.vo.ReturnVO;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
    public ReturnVO<Void> handleJsonError(HttpMessageNotReadableException e){
        return ReturnVO.fail(400, "请求体格式错误，请检查字段类型");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ReturnVO<Void> handleIllegalArgumentException(IllegalArgumentException e){
        return ReturnVO.fail(400, e.getMessage());
    }

    @ExceptionHandler(ChatSessionConflictException.class)
    public ReturnVO<Void> handleChatSessionConflictException(ChatSessionConflictException e){
        return ReturnVO.fail(409,e.getMessage());
    }

    /**
     *处理请求中缺少的必填查询参数的异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ReturnVO<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e){
        return ReturnVO.fail(400,"缺少必要请求参数；" + e.getParameterName());
    }

    /**
     *处理请求参数类型转换失败的异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ReturnVO<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e){
        return ReturnVO.fail(400,"请求体参数错误" + e.getName());
    }

}
