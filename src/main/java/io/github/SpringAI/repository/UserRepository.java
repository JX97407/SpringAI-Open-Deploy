package io.github.SpringAI.repository;


import io.github.SpringAI.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 用户数据访问接口
 *
 * 用于保存用户、根据用户ID查询用户，
 * 以及根据用户名检查用户是否已经存在
 */
public interface UserRepository extends JpaRepository<User,Long> {

    /**
     *根据用户名名称查询用户
     */
    Optional<User> findByUserName(String userName);

    /**
     *判断用户名称是否已经存在
     */
    boolean existsByUserName(String userName);
}
