package org.shiloh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author lxlei
 * @date 2020/11/5 14:07
 * @description
 */
@SpringBootApplication
public class OnlyOneLoginOnSameDeviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlyOneLoginOnSameDeviceApplication.class, args);
    }
}
