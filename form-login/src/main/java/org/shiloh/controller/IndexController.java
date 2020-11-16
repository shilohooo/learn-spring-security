package org.shiloh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lxlei
 * @date 2020/11/3 17:24
 * @description
 */
@RestController
public class IndexController {

    @GetMapping("/index")
    public String toIndexPage() {
        return "Welcome!";
    }
}
