package org.shiloh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author lxlei
 * @date 2020/11/12 17:35
 * @description SpringSecurity Config, {@link EnableGlobalMethodSecurity} 开启注解控制权限支持
 * {@link EnableGlobalMethodSecurity} 这个配置开启了三个注解，分别是：
 * {@link org.springframework.security.access.prepost.PreAuthorize}：方法执行前进行权限检查
 * {@link org.springframework.security.access.prepost.PostAuthorize}：方法执行后进行权限检查
 * {@link org.springframework.security.access.annotation.Secured}：类似于 @PreAuthorize
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("admin")
                .and()
                .withUser("shiloh")
                .password(passwordEncoder().encode("123456"))
                .roles("user");
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
