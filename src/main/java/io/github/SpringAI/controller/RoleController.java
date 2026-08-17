package io.github.SpringAI.controller;

import io.github.SpringAI.dto.RoleOption;
import io.github.SpringAI.enums.ChatRole;
import io.github.SpringAI.vo.ReturnVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * @Description AI角色控制器
 * @Author 刘争伟
 * @Date 2026/8/17 上午8:52
 **/
@RestController
@RequestMapping("/ai")
public class RoleController {

    @GetMapping("roles")
    public ReturnVO<List<RoleOption>>listRoles(){
        List<RoleOption> roles = Arrays.stream(ChatRole.values())
                .map(role -> new RoleOption(
                        role.getCode(),
                        role.getLabel()
                ))
                .toList();

        return ReturnVO.success(roles);
    }
}
