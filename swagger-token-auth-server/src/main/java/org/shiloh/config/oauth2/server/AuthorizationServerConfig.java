package org.shiloh.config.oauth2.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.Collections;

/**
 * @author lxlei
 * @date 2020/11/11 16:44
 * @description 授权服务器配置，{@link EnableAuthorizationServer} 开启授权服务器的自动化配置
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private final TokenStore tokenStore;

    private final ClientDetailsService clientDetailsService;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtAccessTokenConverter jwtAccessTokenConverter;

    public AuthorizationServerConfig(TokenStore tokenStore,
                                     ClientDetailsService clientDetailsService,
                                     AuthenticationManager authenticationManager,
                                     PasswordEncoder passwordEncoder,
                                     JwtAccessTokenConverter jwtAccessTokenConverter) {
        this.tokenStore = tokenStore;
        this.clientDetailsService = clientDetailsService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtAccessTokenConverter = jwtAccessTokenConverter;
    }

    /**
     * 配置access_token参数
     * @author lxlei
     * @date 2020/11/11 16:51
     * @return org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices
     */
    @Bean
    public AuthorizationServerTokenServices authorizationServerTokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setClientDetailsService(clientDetailsService);
        // token存储方案
        defaultTokenServices.setTokenStore(tokenStore);
        // 支持刷新token
        defaultTokenServices.setSupportRefreshToken(true);
        // token有效期：2天
        defaultTokenServices.setAccessTokenValiditySeconds(60 * 60 * 24 * 2);
        // 允许使用refresh_token去刷新access_token的有效期：7天
        defaultTokenServices.setRefreshTokenValiditySeconds(60 * 60 * 24 * 7);
        // token生成链
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Collections.singletonList(jwtAccessTokenConverter));
        defaultTokenServices.setTokenEnhancer(tokenEnhancerChain);
        return defaultTokenServices;
    }

    /**
     * 允许客户端使用表单认证
     * @author lxlei
     * @date 2020/11/11 16:52
     * @param security
     * @return void
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients();
    }

    /**
     * 基于内存配置一个客户端信息测试
     * @author lxlei
     * @date 2020/11/11 16:53
     * @param clients
     * @return void
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                // 客户端id
                .withClient("SHILOH_OAUTH_CLIENT")
                // 客户端密钥
                .secret(passwordEncoder.encode("abc"))
                // 资源id
                .resourceIds("RES_O1")
                // 支持的授权模式
                .authorizedGrantTypes("password", "refresh_token")
                // 授权范围
                .scopes("all")
                // 重定向url
                .redirectUris("http://localhost:8102/index.html");
    }

    /**
     * 配置token的访问端点和生成令牌的服务
     * @author lxlei
     * @date 2020/11/11 16:54
     * @param endpoints
     * @return void
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .tokenServices(authorizationServerTokenServices());
    }
}
