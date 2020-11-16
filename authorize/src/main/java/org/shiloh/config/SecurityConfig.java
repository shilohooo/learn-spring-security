package org.shiloh.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.shiloh.filter.MyLoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lxlei
 * @date 2020/11/4 10:05
 * @description Spring Security配置
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 配置一个用于加密用户密码的类
     * @author lxlei
     * @date 2020/11/4 10:53
     * @return org.springframework.security.crypto.password.PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        DelegatingPasswordEncoder encoder = (DelegatingPasswordEncoder) PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
        encoder.setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());
        return encoder;
    }

//    /**
//     * 配置一个存储于内存的用户信息测试登录认证
//     * @author lxlei
//     * @date 2020/11/4 10:53
//     * @param auth
//     */
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("shiloh")
//                .password("123456")
//                .roles("admin");
//    }

    /**
     * 由于 Spring Security 支持多种数据源，例如内存、数据库、LDAP 等，这些不同来源的数据被共同封装成了一个UserDetailService接口，
     * 任何实现了该接口的对象都可以作为认证数据源。因此我可以通过重写 WebSecurityConfigurerAdapter 中的 userDetailsService 方法，
     * 来提供一个 UserDetailService 实例进而配置多个用户
     * 此配置与configure(AuthenticationManagerBuilder auth)方法都可以配置用户信息，选择其一即可
     * @author lxlei
     * @date 2020/11/4 12:20
     * @return org.springframework.security.core.userdetails.UserDetailsService
     */
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        // 基于内存配置2个角色不同的用户测试授权
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("admin")
                .password("admin")
                .roles("admin")
                .build()
        );
        manager.createUser(User.withUsername("normal")
                .password("123456")
                .roles("user")
                .build()
        );
        return manager;
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
     * 将自定义处理登录认证逻辑的过滤器注入到SpringIOC容器中
     * @author lxlei
     * @date 2020/11/4 10:54
     * @return org.shiloh.filter.MyLoginFilter
     */
    @Bean
    public MyLoginFilter myLoginFilter() throws Exception {
        final MyLoginFilter myLoginFilter = new MyLoginFilter();
        // 登录成功后的处理
        myLoginFilter.setAuthenticationSuccessHandler((req, res, authentication) -> {
            res.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            final PrintWriter writer = res.getWriter();
            // 响应数据
            Map<String, Object> responseData = new HashMap<>(16);
            responseData.put("code", 0);
            responseData.put("message", "登录成功");
            responseData.put("data", authentication);
            final String result = new ObjectMapper().writeValueAsString(responseData);
            writer.write(result);
            writer.flush();
            writer.close();
        });
        // 登录失败后的处理
        myLoginFilter.setAuthenticationFailureHandler((req, res, exception) -> {
            res.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            final PrintWriter writer = res.getWriter();
            // 响应数据
            Map<String, Object> responseData = new HashMap<>(16);
            responseData.put("code", 500);
            responseData.put("message", "用户名或密码错误");
            final String result = new ObjectMapper().writeValueAsString(responseData);
            writer.write(result);
            writer.flush();
            writer.close();
        });
        // 注入认证管理器
        myLoginFilter.setAuthenticationManager(authenticationManagerBean());
        // 设置登录接口地址，默认为/login
        myLoginFilter.setFilterProcessesUrl("/do-login");
        return myLoginFilter;
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
                .csrf()
                .disable();
        // 将自定义处理登录认证的过滤器添加到SpringSecurity的过滤器链中
        // addFilterAt(Filter filter, Class<?> atFilter)此方法用于指定某个过滤器替换掉SpringSecurity原来使用的过滤器
        http.addFilterAt(myLoginFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
