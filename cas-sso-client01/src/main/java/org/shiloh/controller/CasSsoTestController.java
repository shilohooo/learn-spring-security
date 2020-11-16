package org.shiloh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lxlei
 * @date 2020/11/7 17:45
 * @description
 */
@RestController
public class CasSsoTestController {

    @GetMapping("/hello")
    public String sayHello() {
        return "hello";
    }

    @GetMapping("/user/hello")
    public String helloUser() {
        return "hello user";
    }
}
