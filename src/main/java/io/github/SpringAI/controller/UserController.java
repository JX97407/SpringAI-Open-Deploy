/**
 * 类名:UserController
 * 创建人:lzw    创建时间:2026/9/4
 */

package io.github.SpringAI.controller;

import io.github.SpringAI.dto.UserResponse;
import io.github.SpringAI.entity.User;
import io.github.SpringAI.repository.UserRepository;
import io.github.SpringAI.vo.ReturnVO;
import io.github.SpringAI.vo.UserCreateVO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 *  用户管理
 * 〈功能详细描述〉当前阶段只负责创建学习用户，
 *              后续可以继续扩展用户查询、登录和权限控制
 * @author lzw
 */
@RestController
@RequestMapping("ai/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReturnVO<UserResponse> createUser(@Valid @RequestBody UserCreateVO request){
        if (userRepository.existsByUserName(request.getUserName())){
            return ReturnVO.fail(409,"Username已经存在");
        }

        User user = userRepository.save(
                new User(request.getUserName())
        );

        return ReturnVO.success(
                UserResponse.from(user)
        );
    }
}
