package org.shiloh.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lxlei
 * @date 2020/11/3 14:39
 * @description
 */
@RestController
public class HelloController {

    @GetMapping("/hello/{username}")
    public ResponseEntity<String> sayHello(@PathVariable(name = "username") String username) {
        return ResponseEntity.ok(String.format("Hello %s:)", username));
    }
}
