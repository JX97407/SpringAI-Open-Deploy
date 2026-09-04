/**
 * 类名:UserCreateVO
 * 创建人:lzw    创建时间:2026/9/4
 */

package io.github.SpringAI.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 〈功能简述〉创建用户请求对象
 * 〈功能详细描述〉用于接收前端创建用户时提交的用户名，
 *             不直接暴露 User 数据库实体
 * @author lzw
 */

@Data
public class UserCreateVO {

    @NotBlank(message = "username不能为空")
    @Size(max = 50, message = "username长度不能超过50个字符")
    private String userName;
}
