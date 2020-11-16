package org.shiloh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author lxlei
 * @date 2020/11/11 16:36
 * @description SpringSecurity config
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 需要注意的是，在当前案例中将采用 OAuth2 中的 password 模式进行登录，
     * 因此这里还需要明确的提供一个 AuthenticationManager 的 Bean。
     * @author lxlei
     * @date 2020/11/11 16:39
     * @return org.springframework.security.authentication.AuthenticationManager
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
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
        http.requestMatchers()
                .antMatchers(HttpMethod.OPTIONS, "/oauth/**")
                .and()
                .csrf()
                .disable()
                .formLogin()
                .and()
                // 开启SpringSecurity的跨域支持
                .cors();
    }
}
