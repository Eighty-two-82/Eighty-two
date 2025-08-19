package com.springboot.springbootlogindemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalCorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")    //添加映射路径，“/**”表示对所有的路径实行全局跨域访问权限的设置
                        .allowedOrigins("*")    // Which IPs, ports, and domain names are allowed to access
                        .allowCredentials(true)  // Whether to allow sending Cookie information
                        .allowedMethods("GET", "POST", "PUT", "DELETE")     // Which HTTP methods are allowed for cross-origin access
                        .allowedHeaders("*")     // Which Header information is allowed to be carried in HTTP requests
                        .exposedHeaders("*");   // Expose which header information (because cross-origin access cannot get all header information by default)
            }
        };
    }
}
