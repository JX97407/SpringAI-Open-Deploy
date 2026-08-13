package io.github.SpringAI.vo;

/**
 * @Description 通用返回对象类
 * @Author 刘争伟
 * @Date 2026/8/13 上午9:24
 **/
public class ReturnVO<T> {

    private Integer code;
    private String message;
    private T data;

    public ReturnVO(){

    }

    public ReturnVO(Integer code,String message,T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ReturnVO<T> success(T data){
        return new ReturnVO<>(200,"success",data);
    }

    public static <T> ReturnVO<T> fail(String message){
        return  new ReturnVO<>(400,message,null);
    }

    public Integer getCode(){
        return code;
    }

    public void setCode(Integer code){
        this.code = code;
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }
    public T getData(){
        return data;
    }

    public void setData(T data){
        this.data = data;
    }
}
