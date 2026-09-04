/**
 * 类名:UserResponse
 * 创建人:lzw    创建时间:2026/9/4
 */

package io.github.SpringAI.dto;

import io.github.SpringAI.entity.User;

import java.time.LocalDateTime;

/**
 * 〈功能简述〉用户接口返回对象
 * 〈功能详细描述〉用于向前端返回用户的公开信息，
 *              避免直接暴露JPA实体
 * @author lzw
 */
public record UserResponse(
        Long id,
        String userName,
        LocalDateTime createdAt
) {

    public static UserResponse from(User user){
        return new UserResponse(
                user.getId(),
                user.getUserName(),
                user.getCreatedAt()
        );
    }
}
