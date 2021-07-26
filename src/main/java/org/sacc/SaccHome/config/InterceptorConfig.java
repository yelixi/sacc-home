package org.sacc.SaccHome.config;


import org.sacc.SaccHome.UserLoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by 林夕
 * Date 2021/7/12 19:00
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    UserLoginInterceptor userLoginInterceptor(){
        return new UserLoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userLoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login");
    }
}
