package io.github.SpringAI.dto;

/**
 * @Description AI对话返回对象
 * @Author 刘争伟
 * @Date 2026/8/12 下午4:43
 **/
public record ChatResponse(
        String question,
        String answer,
        String model,
        Long durationMs  //调用耗时，单位毫秒
) {

}
