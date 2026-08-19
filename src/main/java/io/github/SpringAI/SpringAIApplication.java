package io.github.SpringAI;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * @Description 主程序
 * @Author 刘争伟
 * @Date 2026/8/12 上午11:05
 **/
@ConfigurationPropertiesScan
@SpringBootApplication
public class SpringAIApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringAIApplication.class,args);
    }
}
