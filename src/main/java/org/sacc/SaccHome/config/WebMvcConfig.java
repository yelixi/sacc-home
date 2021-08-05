package org.sacc.SaccHome.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by 林夕
 * Date 2021/8/5 12:02
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 跨域配置
     *
     * @param registry corsRegistry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .maxAge(3600)
                .allowCredentials(true);
    }
}
