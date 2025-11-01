package com.careapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class GlobalCorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("http://localhost:5173", "http://localhost:5174")
                        .allowCredentials(true)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("*");
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // Get the absolute path to the uploads directory
                File uploadsDir = new File("uploads");
                String uploadsPath = uploadsDir.getAbsolutePath();
                
                // Ensure path ends with separator for proper directory mapping
                if (!uploadsPath.endsWith(File.separator)) {
                    uploadsPath += File.separator;
                }
                
                // Map /uploads/** URLs to the uploads directory
                // Use file:// prefix for absolute path
                registry.addResourceHandler("/uploads/**")
                        .addResourceLocations("file:///" + uploadsPath.replace("\\", "/"))
                        .resourceChain(false);
                
                System.out.println("📁 Static resources configured for: file:///" + uploadsPath.replace("\\", "/"));
                System.out.println("📁 Uploads directory exists: " + uploadsDir.exists());
                if (uploadsDir.exists()) {
                    System.out.println("📁 Uploads directory absolute path: " + uploadsPath);
                }
            }
        };
    }

}
