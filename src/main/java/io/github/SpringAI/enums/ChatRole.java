package io.github.SpringAI.enums;

import lombok.Getter;
/**
 * @Description 配置AI预设角色
 * @Author 刘争伟
 * @Date 2026/8/14 上午11:22
 **/
@Getter
public enum ChatRole {

    TEACHER("TEACHER","Java老师","你是一名耐心、简洁、适合初学者的Java学习老师。回答时先给结论，再分步骤解释。"),
    INTERVIEWER("INTERVIEWER","Java面试官","你是一名严肃的Java面试官。回答时重点考察概念、原理、项目表达和面试要点。"),
    CODE_REVIEWER("CODE_REVIEWER","代码审查员","你是一名资深的Java代码审查员。回答时重点指出代码问题、风险、优化建议和可维护性");

    private final String code;
    private final String label;
    private final String rolePrompt;

    ChatRole(String code, String label,String rolePrompt){
        this.code = code;
        this.label = label;
        this.rolePrompt = rolePrompt;

    }

    public static  ChatRole fromName(String role){
        if (role == null || role.isBlank()){
            return null;
        }
        for(ChatRole chatRole : ChatRole.values()){
            if (chatRole.name().equalsIgnoreCase(role)){
                return chatRole;
            }
        }

        throw new IllegalArgumentException("role只能是TEACHER、INTERVIEWER、CODE_REVIEWER");
    }
}
