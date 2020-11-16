package org.shiloh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lxlei
 * @date 2020/11/4 12:02
 * @description
 */
@RestController
public class TestController {

    /**
     * 任意角色都能访问此接口
     * @author lxlei
     * @date 2020/11/4 12:06
     * @return java.lang.String
     */
    @GetMapping("/hello")
    public String hello() {
        return "这是所有角色都能看到的信息";
    }

    /**
     * 具有[user]角色可以访问的接口，且所有[user]角色能访问的接口，[admin]角色都可以访问
     * @author lxlei
     * @date 2020/11/4 12:06
     * @return java.lang.String
     */
    @GetMapping("/user/hello")
    public String helloUser() {
        return "这是具有[user]角色可以看到的信息";
    }

    /**
     * 具有[admin]角色才能访问的接口
     * @author lxlei
     * @date 2020/11/4 12:06
     * @return java.lang.String
     */
    @GetMapping("/admin/hello")
    public String helloAdmin() {
        return "这是具有[admin]角色才能看到的信息";
    }
}
