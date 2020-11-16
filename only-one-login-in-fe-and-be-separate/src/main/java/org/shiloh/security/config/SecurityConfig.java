package org.shiloh.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.session.StandardSession;
import org.shiloh.security.filter.MyLoginFilter;
import org.shiloh.security.handler.MyAuthenticationFailureHandler;
import org.shiloh.security.handler.MyAuthenticationSuccessHandler;
import org.shiloh.security.service.MyUserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author lxlei
 * @date 2020/11/4 14:35
 * @description spring security 配置
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final MyUserDetailsServiceImpl myUserDetailsService;

    private final MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    private final MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    public SecurityConfig(MyUserDetailsServiceImpl myUserDetailsService,
                          MyAuthenticationSuccessHandler myAuthenticationSuccessHandler,
                          MyAuthenticationFailureHandler myAuthenticationFailureHandler) {
        this.myUserDetailsService = myUserDetailsService;
        this.myAuthenticationSuccessHandler = myAuthenticationSuccessHandler;
        this.myAuthenticationFailureHandler = myAuthenticationFailureHandler;
    }

    @Bean
    public SessionRegistryImpl sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**
     * 在Spring Security中，通过监听session的销毁事件来及时的清理session的记录。
     * 用户从不同的浏览器登录后，都会有对应的session，当用户注销登录之后，session就会失效，
     * 但是默认的失效是通过调用 {@link StandardSession#invalidate()} 方法来实现的:
     * 这一个失效事件无法被Spring容器感知到，进而导致当用户注销登录之后，Spring Security 没有及时清理会话信息表，以为用户还在线，
     * 进而导致用户无法重新登录进来，为了解决这一问题，需要提供一个 {@link HttpSessionEventPublisher}
     * 这个类实现了 {@link javax.servlet.http.HttpSessionListener} 接口，在该Bean中，可以将session创建以及销毁的事件及时感知到，
     * 并且调用Spring中的事件机制将相关的创建和销毁事件发布出去，进而被 Spring Security 感知到
     * @author lxlei
     * @date 2020/11/5 14:19
     * @return org.springframework.security.web.session.HttpSessionEventPublisher
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    /**
     * 注入自定义的登录认证过滤器
     * @author lxlei
     * @date 2020/11/5 15:28
     * @return org.shiloh.security.filter.MyLoginFilter
     */
    @Bean
    public MyLoginFilter myLoginFilter() throws Exception {
        MyLoginFilter myLoginFilter = new MyLoginFilter(sessionRegistry());
        // 设置认证成功处理器
        myLoginFilter.setAuthenticationSuccessHandler(myAuthenticationSuccessHandler);
        // 设置认证失败处理器
        myLoginFilter.setAuthenticationFailureHandler(myAuthenticationFailureHandler);
        // 设置登录接口地址
        myLoginFilter.setFilterProcessesUrl("/do-login");
        // 设置认证管理器
        myLoginFilter.setAuthenticationManager(authenticationManagerBean());
        // 设置session认证策略
        final ConcurrentSessionControlAuthenticationStrategy strategy =
                new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
        // 设置同一用户最大会话数会1，防止多端登录
        strategy.setMaximumSessions(1);
        myLoginFilter.setSessionAuthenticationStrategy(strategy);
        return myLoginFilter;
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
                .loginPage("/login.html")
                .permitAll()
                .and()
                .csrf()
                .disable();
        // 创建一个 ConcurrentSessionFilter 的实例，代替系统默认的即可。在创建新的 ConcurrentSessionFilter 实例时，需要两个参数：
        // sessionRegistry 就是上面提供的 SessionRegistryImpl 实例。
        // 第二个参数，是一个处理 session 过期后的回调函数，也就是说，当用户被另外一个登录踢下线之后，要给什么样的下线提示，就在这里来完成。
        http.addFilterAt(new ConcurrentSessionFilter(sessionRegistry(), event -> {
            final HttpServletResponse response = event.getResponse();
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            final PrintWriter writer = response.getWriter();
            writer.write(new ObjectMapper()
                    .writeValueAsString(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("您已在另一台设备登录，本次登录已下线!")
                    )
            );
            writer.flush();
            writer.close();
        }), ConcurrentSessionFilter.class);
        http.addFilterAt(myLoginFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
