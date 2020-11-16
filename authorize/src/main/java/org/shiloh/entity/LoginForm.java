package org.shiloh.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lxlei
 * @date 2020/11/4 10:17
 * @description
 */
@Data
public class LoginForm implements Serializable {

    /**
     * login username
     */
    private String username;

    /**
     * user password
     */
    private String password;
}
