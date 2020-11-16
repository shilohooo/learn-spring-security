package org.shiloh;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lxlei
 * @date 2020/11/13 11:37
 * @description
 */
public class MultipleEncryptorsTests {

    @Test
    public void encrypt() {
        Map<String, PasswordEncoder> encoders = new HashMap<>(16);
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("MD5", new MessageDigestPasswordEncoder("MD5"));
        encoders.put("noop", NoOpPasswordEncoder.getInstance());
        DelegatingPasswordEncoder bcryptEncoder = new DelegatingPasswordEncoder("bcrypt", encoders);
        DelegatingPasswordEncoder md5Encoder = new DelegatingPasswordEncoder("MD5", encoders);
        DelegatingPasswordEncoder noopEncoder = new DelegatingPasswordEncoder("noop", encoders);
        String passwordEncryptedByBcryptEncoder = bcryptEncoder.encode("123456");
        String passwordEncryptedByMd5Encoder = md5Encoder.encode("123456");
        String passwordEncryptedByNoopEncoder = noopEncoder.encode("123456");
        System.out.println("passwordEncryptedByBcryptEncoder = " + passwordEncryptedByBcryptEncoder);
        System.out.println("passwordEncryptedByMd5Encoder = " + passwordEncryptedByMd5Encoder);
        System.out.println("passwordEncryptedByNoopEncoder = " + passwordEncryptedByNoopEncoder);
    }
}
