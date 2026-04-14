package edu.kust.facedeepseek.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 必须加这个注解，让Spring Boot加载该配置
public class WebConfig implements WebMvcConfigurer {

    // 从配置文件读取你实际的文件保存目录（D:/JakartaEE/uploads）
    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 关键配置：将 "/uploads/**" URL 映射到实际的文件目录
        // 注意："file:" 前缀不能少（表示本地磁盘目录），路径结尾必须加 "/"
        registry.addResourceHandler("/uploads/**")
                // 映射到：file:D:/JakartaEE/uploads/
                .addResourceLocations("file:" + uploadDir + "/");
    }
}