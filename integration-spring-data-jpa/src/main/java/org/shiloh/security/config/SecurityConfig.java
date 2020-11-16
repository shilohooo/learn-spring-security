package org.shiloh.security.config;

import org.shiloh.security.service.MyUserDetailsServiceImpl;
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
 * @date 2020/11/4 14:35
 * @description spring security 配置
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final MyUserDetailsServiceImpl myUserDetailsService;

    public SecurityConfig(MyUserDetailsServiceImpl myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    /**
     * 加密类
     * @author lxlei
     * @date 2020/11/4 14:35
     * @return org.springframework.security.crypto.password.PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 角色继承关系配置
     * 上级可能具备下级的所有权限，使用角色继承只需要在 SecurityConfig 中配置角色继承关系即可
     * @author lxlei
     * @date 2020/11/4 14:07
     * @return org.springframework.security.access.hierarchicalroles.RoleHierarchy
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_admin > ROLE_user");
        return roleHierarchy;
    }

    /**
     * 使用自定义的UserDetailsService实现类来配置用户数据源
     * @author lxlei
     * @date 2020/11/4 16:38
     * @param auth
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 注入自定义加载用户的类
        auth.userDetailsService(myUserDetailsService);
    }

    /**
     * 认证和授权相关配置
     * @author lxlei
     * @date 2020/11/4 11:18
     * @param http
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // 配置需要某个角色才能访问的接口地址，使用ant匹配规则，/xxx/**代表匹配多层路径，例如/admin/xxx/xxx
                // 此处配置应该位于anyRequest().authenticated()配置前，具体请参考：AbstractRequestMatcherRegistry
                // 注意代码中配置的三条规则的顺序非常重要，和 Shiro 类似，Spring Security在匹配的时候也是按照从上往下的顺序来匹配，
                // 一旦匹配到了就不继续匹配了，所以拦截规则的顺序不能写错。
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
