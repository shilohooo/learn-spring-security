package org.shiloh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @author lxlei
 * @date 2020/11/13 11:42
 * @description
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello(Principal principal) {
        return "Hello" + principal.getName();
    }
}
