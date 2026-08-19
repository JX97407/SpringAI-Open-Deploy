package io.github.SpringAI.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description AI业务配置
 * @Author 刘争伟
 * @Date 2026/8/18 下午5:02
 **/
@ConfigurationProperties(prefix = "app.ai")

public record AIProperties(
        String systemPrompt,
        Memory memory
) {

    //对应 application.yml 中的 app.ai.memory 配置
    public record Memory(
            int maxMessages
    ){

    }
}
