package org.shiloh.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author lxlei
 * @date 2020/11/16 10:27
 * @description Spring Security Configuration
 */
@Configuration
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
                // 配置admin角色才能访问的请求路径
                .antMatchers("/admin/**")
                .hasRole("admin")
                // 配置user角色才能访问的请求路径
                // 注意代码中配置的三条规则的顺序非常重要，和 Shiro 类似，Spring Security 在匹配的时候也是按照从上往下的顺序来匹配，
                // 一旦匹配到了就不继续匹配了，所以拦截规则的顺序不能写错。
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

    /**
     * 配置角色继承
     * 注意，在配置时，需要给角色手动加上 ROLE_ 前缀。下面的配置表示 ROLE_admin 自动具备 ROLE_user 的权限。
     * @author lxlei
     * @date 2020/11/16 10:35
     * @return org.springframework.security.access.hierarchicalroles.RoleHierarchy
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_admin > ROLE_user");
        return roleHierarchy;
    }
}
