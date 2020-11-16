package org.shiloh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lxlei
 * @date 2020/11/12 16:05
 * @description
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "hello";
    }
}
