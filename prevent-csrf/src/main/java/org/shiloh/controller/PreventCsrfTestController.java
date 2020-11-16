package org.shiloh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lxlei
 * @date 2020/11/7 9:04
 * @description
 */
@RestController
public class PreventCsrfTestController {

    @PostMapping("/transfer")
    public void transferMoney(String username, Double money) {
        System.out.println("username = " + username);
        System.out.println("money = " + money);
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello :)";
    }
}
