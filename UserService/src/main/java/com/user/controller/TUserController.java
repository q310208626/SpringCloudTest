package com.user.controller;

import com.user.bean.TUser;
import com.user.bean.TUserRole;
import com.user.service.ITUserRoleService;
import com.user.service.ITUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author Soga
 * @since 2024-10-18
 */
@RestController
@RequestMapping("/user")
public class TUserController {

    @Autowired
    private ITUserService userService;

    @Autowired
    private ITUserRoleService userRoleService;

    @GetMapping("/{userId}")
    public ResponseEntity<TUser> getUser(@PathVariable("userId") Integer userId) {
        TUser tUser = Optional.ofNullable(userService.getById(userId)).orElse(new TUser());
        tUser.setPassword(null);
        return ResponseEntity.ofNullable(tUser);
    }

    @PostMapping
    @Secured("USER_MANAGE")
    public ResponseEntity addUser(@RequestBody TUser tUser) {
        userService.save(tUser);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<TUser> deleteUser(@PathVariable("userId") Integer userId) {
        TUser deleteUser = new TUser();
        deleteUser.setUserId(userId);
        boolean result = userService.removeById(deleteUser);
        if (result) {
            return ResponseEntity.ok(deleteUser);
        } else {
            return ResponseEntity.status(HttpStatus.METHOD_FAILURE).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<TUser> login(@RequestBody TUser tUser) {
        return ResponseEntity.ok(tUser);
    }

    @PostMapping("/{userId}/roles")
    @Secured("USER_MANAGE")
    public ResponseEntity<List<Integer>> userRoleAdd(@PathVariable("userId") Integer userId, @RequestBody List<Integer> roleIds) {
        List<TUserRole> userRoleList = roleIds.stream().map(roleId -> {
            TUserRole tUserRole = new TUserRole();
            tUserRole.setUserId(userId);
            tUserRole.setRoleId(roleId);
            return tUserRole;
        }).collect(Collectors.toList());

        userRoleService.saveBatch(userRoleList);
        return ResponseEntity.ok(roleIds);
    }

}
