package org.shiloh.config.swagger2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author lxlei
 * @date 2020/11/11 17:23
 * @description Swagger2 Configuration {@link EnableSwagger2} 开启Swagger2自动化配置
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

    /**
     * 配置映射路径和要扫描的接口的位置。
     * @author lxlei
     * @date 2020/11/12 15:25
     * @return springfox.documentation.spring.web.plugins.Docket
     */
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.shiloh.controller"))
                .paths(PathSelectors.any())
                .build()
                .securityContexts(Collections.singletonList(securityContexts()))
                .securitySchemes(Collections.singletonList(securitySchemes()))
                // 配置Swagger2 文档网站的信息，例如网站的 title，网站的描述，联系人的信息，使用的协议等等。
                .apiInfo(new ApiInfoBuilder()
                        .description("接口文档描述信息")
                        .title("Swagger2请求带Token测试接口文档")
                        .contact(new Contact("shilohooo", "https://github.com/shilohooo",
                                "lixiaolei595@gmail.com"))
                        .version("v1.0")
                        .license("Apache2.0")
                        .build()
                );
    }

    /**
     * 获取授权范围
     * @author lxlei
     * @date 2020/11/12 15:45
     * @return springfox.documentation.service.AuthorizationScope[]
     */
    private AuthorizationScope[] getAuthorizationScopes() {
        return new AuthorizationScope[]{
                new AuthorizationScope("all", "all scope")
        };
    }

    /**
     * 配置全局参数，这里的配置是一个名为 Authorization 的请求头（OAuth2 中需要携带的请求头）。
     * @author lxlei
     * @date 2020/11/12 15:26
     * @return springfox.documentation.service.SecurityScheme
     */
    private SecurityScheme securitySchemes() {
        // 配置获取OAuth2 access_token的请求地址
        GrantType grantType = new ResourceOwnerPasswordCredentialsGrant("http://localhost:8101/oauth/token");
        return new OAuthBuilder().name("OAuth2")
                .grantTypes(Collections.singletonList(grantType))
                .scopes(Arrays.asList(getAuthorizationScopes()))
                .build();
    }

    /**
     * 配置有哪些请求需要携带 Token，这里配置了所有请求。
     * @author lxlei
     * @date 2020/11/12 15:26
     * @return springfox.documentation.spi.service.contexts.SecurityContext
     */
    private SecurityContext securityContexts() {
        return SecurityContext.builder()
                .securityReferences(Collections.singletonList(new SecurityReference("OAuth2",
                        getAuthorizationScopes()))
                )
                .forPaths(PathSelectors.any())
                .build();
    }

}
