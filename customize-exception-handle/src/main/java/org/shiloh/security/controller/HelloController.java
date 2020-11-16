package org.shiloh.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @author lxlei
 * @date 2020/11/13 14:24
 * @description
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello(Principal principal) {
        return "Hello " + principal.getName();
    }

    @GetMapping("/admin/hello")
    public String helloAdmin() {
        return "Hello admin";
    }

    @GetMapping("/user/hello")
    public String helloUser() {
        return "Hello user";
    }
}
