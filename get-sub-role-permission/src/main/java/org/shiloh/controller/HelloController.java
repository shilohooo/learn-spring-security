package org.shiloh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lxlei
 * @date 2020/11/16 10:29
 * @description
 */
@RestController
public class HelloController {

    /**
     * 此接口只要认证成功就能访问
     * @author lxlei
     * @date 2020/11/16 10:31
     * @return java.lang.String
     */
    @GetMapping("/hello")
    public String sayHello() {
        return "hello";
    }

    /**
     * 此接口需要具备[user]角色才能访问，且所有[user]角色能够访问的接口[admin]角色都能访问
     * @author lxlei
     * @date 2020/11/16 10:31
     * @return java.lang.String
     */
    @GetMapping("/user/hello")
    public String helloUser() {
        return "user";
    }

    /**
     * 此接口只有[admin]角色可以访问
     * @author lxlei
     * @date 2020/11/16 10:32
     * @return java.lang.String
     */
    @GetMapping("/admin/hello")
    public String helloAdmin() {
        return "admin";
    }
}
