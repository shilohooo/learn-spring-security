package org.shiloh.service;

import org.shiloh.authentication.MyWebAuthenticationDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * @author lxlei
 * @date 2020/11/5 11:50
 * @description
 */
@Service
public class HelloService {

    public void printCurrentlyLoggedUserIpAddress() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyWebAuthenticationDetails details = (MyWebAuthenticationDetails) authentication.getDetails();
        System.out.println(details);
    }
}
