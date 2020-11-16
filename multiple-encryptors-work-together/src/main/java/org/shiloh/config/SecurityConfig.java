package org.shiloh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * @author lxlei
 * @date 2020/11/13 11:43
 * @description
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 配置3个密码经过不同加密器加密的用户用于测试
     * 关于加密器的配置请参考：{@link org.springframework.security.crypto.password.DelegatingPasswordEncoder}
     * 关于加密器是如何注入的请参考：{@link DaoAuthenticationProvider#DaoAuthenticationProvider()}
     * @author lxlei
     * @date 2020/11/13 11:46
     * @return org.springframework.security.core.userdetails.UserDetailsService
     */
    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("admin")
                .password("{bcrypt}$2a$10$D991YAONUw716BAQSd8Xe.OTvyp38jQgYB0l2h7ufJMWGkSEcBCwm")
                .roles("admin")
                .build()
        );
        manager.createUser(User.withUsername("shiloh")
                .password("{MD5}{bVAqpvzufo1Vb2RCR3ccamekv/uBztEZtqDA1c63X48=}75c68d5971ea2d3c10662b2737e6e5b8")
                .roles("user")
                .build()
        );
        manager.createUser(User.withUsername("tom")
                .password("{noop}123456")
                .roles("user")
                .build()
        );
        return manager;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .and()
                .csrf()
                .disable();
    }
}
