package org.shiloh.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.shiloh.service.HelloService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lxlei
 * @date 2020/11/5 11:52
 * @description
 */
@RestController
public class HelloController {

    private final HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("/ip-address")
    public String printIpAddress() throws JsonProcessingException {
        helloService.printCurrentlyLoggedUserIpAddress();
        return new ObjectMapper()
                .writeValueAsString(ResponseEntity.ok("success"));
    }
}
