package org.shiloh.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lxlei
 * @date 2020/11/5 14:08
 * @description
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() throws JsonProcessingException {
        return new ObjectMapper()
                .writeValueAsString(ResponseEntity.ok("Hello"));
    }
}
