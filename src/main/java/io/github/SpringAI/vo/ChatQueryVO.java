package io.github.SpringAI.vo;

/**
 * @Description AI对话请求对象
 * @Author 刘争伟
 * @Date 2026/8/12 下午5:56
 **/

public class ChatQueryVO {

    private String question;

    public ChatQueryVO() {

    }

    public String getQuestion(){
        return question;
    }

    public void setQuestion(String question){
        this.question = question;
    }
}
