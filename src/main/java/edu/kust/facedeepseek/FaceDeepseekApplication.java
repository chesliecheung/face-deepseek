package edu.kust.facedeepseek;

import org.mybatis.spring.annotation.MapperScan; // 导入MapperScan注解
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// 关键：扫描Mapper接口所在包（edu.kust.facedeepseek.mapper）
@MapperScan("edu.kust.facedeepseek.mapper")

public class FaceDeepseekApplication {

    public static void main(String[] args) {
        SpringApplication.run(FaceDeepseekApplication.class, args);
    }

}