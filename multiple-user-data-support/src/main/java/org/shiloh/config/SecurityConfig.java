package org.shiloh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;

/**
 * @author lxlei
 * @date 2020/11/13 15:57
 * @description Spring Security configuration
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 注入2个基于内存的用户信息测试对接不同的用户数据源
     * @author lxlei
     * @date 2020/11/13 16:03
     * @return org.springframework.security.core.userdetails.UserDetailsService
     */
    @Bean
    @Primary
    public UserDetailsService userDetailsService01() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("admin")
                .build()
        );
        return manager;
    }

    @Bean
    public UserDetailsService userDetailsService02() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("shiloh")
                .password(passwordEncoder().encode("123456"))
                .roles("user")
                .build()
        );
        return manager;
    }

    /**
     * 将用户数据源添加到具体的Provider进行认证处理，并把Provider设置到ProvideManager
     * @author lxlei
     * @date 2020/11/13 16:03
     * @return org.springframework.security.authentication.AuthenticationManager
     */
    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        DaoAuthenticationProvider provider01 = new DaoAuthenticationProvider();
        provider01.setPasswordEncoder(passwordEncoder());
        provider01.setUserDetailsService(userDetailsService01());

        DaoAuthenticationProvider provider02 = new DaoAuthenticationProvider();
        provider02.setPasswordEncoder(passwordEncoder());
        provider02.setUserDetailsService(userDetailsService02());

        return new ProviderManager(Arrays.asList(provider01, provider02));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**")
                .hasRole("admin")
                .antMatchers("/user/**")
                .hasRole("user")
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .permitAll()
                .and()
                .csrf()
                .disable();
    }
}
