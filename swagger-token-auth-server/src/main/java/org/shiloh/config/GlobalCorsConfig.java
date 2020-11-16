package org.shiloh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author lxlei
 * @date 2020/11/12 15:38
 * @description 全局跨域配置
 */
@Configuration
public class GlobalCorsConfig {

    /**
     * 跨域配置
     * @author lxlei
     * @date 2020/11/12 15:41
     * @return org.springframework.web.filter.CorsFilter
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 支持用户凭证
        corsConfiguration.setAllowCredentials(true);
        // 添加允许的请求来源
        corsConfiguration.addAllowedOrigin("*");
        // 添加允许的请求头内容
        corsConfiguration.addAllowedHeader("*");
        // 添加允许的请求方法：POST、GET、DELETE、PUT、PATCH、OPTIONS...
        corsConfiguration.addAllowedMethod("*");
        // 将此跨域配置应用于所有请求路径中
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
