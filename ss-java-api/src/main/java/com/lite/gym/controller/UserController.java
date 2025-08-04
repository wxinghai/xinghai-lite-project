package com.lite.gym.controller;

import com.lite.gym.model.User;
import com.lite.gym.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取所有用户列表
     */
    @GetMapping("/list")
    public List<User> listUsers() {
        return userService.list();
    }

    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getById(id);
    }

    /**
     * 新增用户
     */
    @PostMapping("/add")
    public boolean addUser(@RequestBody User user) {
        return userService.save(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public boolean updateUser(@RequestBody User user) {
        return userService.updateById(user);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/delete/{id}")
    public boolean deleteUser(@PathVariable Long id) {
        return userService.removeById(id);
    }
}
