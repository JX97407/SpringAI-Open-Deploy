package io.github.SpringAI.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 通用返回对象类
 * @Author 刘争伟
 * @Date 2026/8/13 上午9:24
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnVO<T> {

    private Integer code;
    private String message;
    private T data;


    public static <T> ReturnVO<T> success(T data){
        return new ReturnVO<>(200,"success",data);
    }

    public static <T> ReturnVO<T> fail(String message){
        return  new ReturnVO<>(400,message,null);
    }

    public static <T> ReturnVO<T> fail(Integer code,String message){
        return new ReturnVO<>(code, message, null);
    }
}
