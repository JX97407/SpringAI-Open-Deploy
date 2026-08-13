package io.github.SpringAI.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @Description AI对话请求对象类
 * @Author 刘争伟
 * @Date 2026/8/12 下午5:56
 **/
@Data
public class ChatQueryVO {

@NotBlank(message = "question不能为空")
@Size(max = 2000,message = "question长度不能超过2000个字符")
    private String question;
}
