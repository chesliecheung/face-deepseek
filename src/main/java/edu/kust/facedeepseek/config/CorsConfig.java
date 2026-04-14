package edu.kust.facedeepseek.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration conf = new CorsConfiguration();

        // 指定前端运行的地址，比如 http://localhost:5500 或 http://localhost:8080
        conf.setAllowedOrigins(Arrays.asList("http://localhost:5500", "http://localhost:8080"));

        // 允许发送 Cookie
        conf.setAllowCredentials(true);

        // 允许请求头和方法
        conf.addAllowedHeader("*");
        conf.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", conf);



        return new CorsFilter(source);
    }
}
