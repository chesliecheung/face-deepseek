package edu.kust.facedeepseek.controller;

import edu.kust.facedeepseek.util.VerificationCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
public class MailController {

    @Autowired
    private JavaMailSender javaMailSender;

    @PostMapping("/mail")
    @ResponseBody
    public String sendCode(@RequestParam String email) {
        String code = VerificationCode.generateNumberCode();
        VerificationCode.saveCode(email, code, 1);  // 保存验证码，过期时间1分钟

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("2136947133@qq.com");
        message.setTo(email);
        message.setSubject("注册xx系统验证码");
        message.setText("您的验证码是：" + code + "(有效期1分钟)");
        javaMailSender.send(message);

        return "success";
    }

    // 新增验证端点
    @PostMapping("/verifyCode")
    public VerifyResponse verify(@RequestBody VerifyRequest request) {
        boolean valid = VerificationCode.verifyCode(request.getKey(), request.getCode());
        return new VerifyResponse(valid);
    }

    // 请求类
    public static class VerifyRequest {
        private String key;
        private String code;

        // getter/setter
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
    }

    // 响应类
    public static class VerifyResponse {
        private boolean valid;

        public VerifyResponse(boolean valid) { this.valid = valid; }
        public boolean isValid() { return valid; }
    }
}