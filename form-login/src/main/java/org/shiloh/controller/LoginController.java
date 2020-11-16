package org.shiloh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lxlei
 * @date 2020/11/3 17:47
 * @description
 */
@RestController
public class LoginController {

    @PostMapping("/login-failed")
    public String loginFailed() {
        return "登录失败啦";
    }

    @GetMapping("/logged-out")
    public String loggedOut() {
        return "注销成功";
    }
}
