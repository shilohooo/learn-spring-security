package org.shiloh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lxlei
 * @date 2020/11/4 17:22
 * @description
 */
@RestController
public class HelloController {

    /**
     * 此接口只要认证后就可以访问，无论是通过用户名密码认证还是通过自动登录认证，只要认证了，就可以访问
     * @author lxlei
     * @date 2020/11/5 9:28
     * @return java.lang.String
     */
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, enjoy in spring security:)";
    }

    /**
     * 此接口必须要用户名密码认证之后才能访问，如果用户是通过自动登录认证的，则必须重新输入用户名密码才能访问该接口。
     * @author lxlei
     * @date 2020/11/5 9:28
     * @return java.lang.String
     */
    @GetMapping("/admin-msg")
    public String getAdminMsg() {
        return "Hello admin";
    }

    /**
     * 此接口必须是通过自动登录认证后才能访问，如果用户是通过用户名/密码认证的，则无法访问该接口。
     * @author lxlei
     * @date 2020/11/5 9:29
     * @return java.lang.String
     */
    @GetMapping("/test-remember-me")
    public String testRememberMe() {
        return "remember me...";
    }
}
