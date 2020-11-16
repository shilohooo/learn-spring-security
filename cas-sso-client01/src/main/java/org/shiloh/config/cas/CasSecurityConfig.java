package org.shiloh.config.cas;

import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.TicketValidator;
import org.shiloh.autoconfigure.CasClientProperties;
import org.shiloh.autoconfigure.CasServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.util.Collections;

/**
 * @author lxlei
 * @date 2020/11/7 17:16
 * @description cas configuration
 */
@Configuration
public class CasSecurityConfig {

    private final CasServerProperties casServerProperties;

    private final CasClientProperties casClientProperties;

    private final UserDetailsService userDetailsService;

    public CasSecurityConfig(CasServerProperties casServerProperties, CasClientProperties casClientProperties, UserDetailsService userDetailsService) {
        this.casServerProperties = casServerProperties;
        this.casClientProperties = casClientProperties;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        // 设置客户端登录请求路径，这个地址是在CAS Server上登录成功后，重定向的地址。
        serviceProperties.setService(casClientProperties.getLoginUrl());
        return serviceProperties;
    }

    /**
     * CAS 验证入口
     * @author lxlei
     * @date 2020/11/7 17:27
     * @return org.springframework.security.web.AuthenticationEntryPoint
     */
    @Bean
    @Primary
    public AuthenticationEntryPoint authenticationEntryPoint() {
        CasAuthenticationEntryPoint authenticationEntryPoint = new CasAuthenticationEntryPoint();
        // 设置服务器单点登录路径
        authenticationEntryPoint.setLoginUrl(casServerProperties.getLoginUrl());
        // 设置服务属性，当登录验证成功后cas server可以根据此属性知道要跳转到哪里
        authenticationEntryPoint.setServiceProperties(serviceProperties());
        return authenticationEntryPoint;
    }

    /**
     * ticket校验器：
     * cas客户端拿到ticket后需要到cas服务器上进行校验，默认校验地址：https://domain:port/cas/proxyValidate?ticket=xxx
     * @author lxlei
     * @date 2020/11/7 17:29
     * @return org.jasig.cas.client.validation.TicketValidator
     */
    @Bean
    public TicketValidator ticketValidator() {
        return new Cas20ProxyTicketValidator(casServerProperties.getPrefix());
    }

    /**
     * cas 登录验证逻辑处理器，扩展请参考 {@link AuthenticationProvider}
     * @author lxlei
     * @date 2020/11/7 17:30
     * @return org.springframework.security.cas.authentication.CasAuthenticationProvider
     */
    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setServiceProperties(serviceProperties());
        provider.setTicketValidator(ticketValidator());
        provider.setUserDetailsService(userDetailsService);
        provider.setKey("SHILOH595");
        return provider;
    }

    /**
     * CAS 认证过滤器：过滤器将请求拦截下来之后，交由 {@link CasAuthenticationProvider} 来做具体处理。
     * @author lxlei
     * @date 2020/11/7 17:33
     * @return org.springframework.security.cas.web.CasAuthenticationFilter
     */
    @Bean
    public CasAuthenticationFilter casAuthenticationFilter(AuthenticationProvider authenticationProvider) {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setServiceProperties(serviceProperties());
        filter.setAuthenticationManager(new ProviderManager(Collections.singletonList(authenticationProvider)));
        return filter;
    }

    /**
     * 处理注销登录的过滤器
     * 接受 CAS Server 发出的注销请求，所有的注销请求都将从 CAS Client 转发到 CAS Server，
     * CAS Server 处理完后，会通知所有的 CAS Client 注销登录。
     * @author lxlei
     * @date 2020/11/7 17:34
     * @return org.jasig.cas.client.session.SingleSignOutFilter
     */
    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        SingleSignOutFilter filter = new SingleSignOutFilter();
        filter.setIgnoreInitConfiguration(true);
        return filter;
    }

    /**
     * 此配置将注销请求转发到 CAS Server
     * @author lxlei
     * @date 2020/11/7 17:35
     * @return org.springframework.security.web.authentication.logout.LogoutFilter
     */
    @Bean
    public LogoutFilter logoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter(casServerProperties.getLogoutUrl(),
                new SecurityContextLogoutHandler());
        // 设置注销登录url
        logoutFilter.setFilterProcessesUrl(casClientProperties.getLogoutRelativeUrl());
        return logoutFilter;
    }
}
