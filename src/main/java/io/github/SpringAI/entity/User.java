/**
 * 类名:User
 * 创建人:lzw    创建时间:2026/9/3
 */

package io.github.SpringAI.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 〈功能简述〉用户实体，对应数据库中的app_user表
 * 〈功能详细描述〉当前阶段用于建立用户与聊天会话之间的归属关系，后续可以继续扩展登录信息、权限和用户隔离能力
 * @author lzw
 */
@Getter
@Entity
@Table(name = "app_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(name = "user_name", nullable = false, unique = true, length = 50)
    private String userName;

    @Column(name= "createdAt", nullable = false)
    private LocalDateTime createdAt;

    public User(String userName){
        this.userName = userName;
    }

    @PrePersist
    private void beforeInsert(){
        this.createdAt = LocalDateTime.now();
    }
}
