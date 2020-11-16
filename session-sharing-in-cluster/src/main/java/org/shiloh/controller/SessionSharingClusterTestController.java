package org.shiloh.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author lxlei
 * @date 2020/11/6 17:22
 * @description
 */
@RestController
public class SessionSharingClusterTestController {

    @Value("${server.port}")
    private Integer serverPort;

    @GetMapping("/set")
    public String setUsernameToSessionAttribute(HttpSession session) {
        session.setAttribute("username", "shiloh");
        return String.format("success, current server port is %s", serverPort);
    }

    @GetMapping("/get")
    public String getUsernameInSession(HttpSession session) {
        return String.format("%s : %s", session.getAttribute("username").toString(), serverPort);
    }
}
