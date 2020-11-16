package org.shiloh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lxlei
 * @date 2020/11/6 16:57
 * @description
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello";
    }
}
