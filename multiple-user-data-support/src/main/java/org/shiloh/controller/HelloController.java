package org.shiloh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lxlei
 * @date 2020/11/13 15:55
 * @description
 */
@RestController
public class HelloController {

    /**
     * 任意角色都能访问此接口
     * @author lxlei
     * @date 2020/11/4 12:06
     * @return java.lang.String
     */
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    /**
     * 具有[user]角色可以访问的接口
     * @author lxlei
     * @date 2020/11/4 12:06
     * @return java.lang.String
     */
    @GetMapping("/user/hello")
    public String helloUser() {
        return "user";
    }

    /**
     * 具有[admin]角色才能访问的接口
     * @author lxlei
     * @date 2020/11/4 12:06
     * @return java.lang.String
     */
    @GetMapping("/admin/hello")
    public String helloAdmin() {
        return "admin";
    }

}
