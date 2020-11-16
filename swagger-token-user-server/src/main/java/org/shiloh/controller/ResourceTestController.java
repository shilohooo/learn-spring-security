package org.shiloh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lxlei
 * @date 2020/11/11 17:15
 * @description
 */
@RestController
public class ResourceTestController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello";
    }

    @GetMapping("/user/hello")
    public String helloUser() {
        return "Hello user";
    }

    @GetMapping("/admin/hello")
    public String helloAdmin() {
        return "Hello admin";
    }
}
