package edu.kust.facedeepseek.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.PostConstruct;

@RestController // 添加控制器注解
public class ValueController { // 显式声明为public

    @Value("${deepseek.api.url}")
    private String url;

    public ValueController() {
        // 构造方法中无法获取注入的属性，此时url为null
        System.out.println("构造方法中：url = " + url);
    }

    // 初始化方法，在属性注入完成后执行
    @PostConstruct
    public void init() {
        System.out.println("属性注入后：url = " + url);
    }

    @GetMapping("url")
    public String getUrl() {
        return url;
    }
}