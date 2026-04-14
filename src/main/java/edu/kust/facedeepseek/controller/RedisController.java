package edu.kust.facedeepseek.controller;

import edu.kust.facedeepseek.util.VerificationCode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/redis")
public class RedisController {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 生成并存储验证码到Redis（可用于注册、登录等场景的验证码发放）
     * 访问方式：GET http://localhost:8090/redis/generateCode?key=xxx（key可以是邮箱、手机号等标识）
     */
    @GetMapping("generateCode")
    public String generateCode(@RequestParam(required = true, value = "key") String key) {
        if (key.trim().isEmpty()) {
            return "参数错误：key 不能为空字符串";
        }
        try {
            // 生成6位数字验证码，可根据需要调整验证码长度，比如 VerificationCode.generateNumberCode(4) 生成4位
            String code = VerificationCode.generateNumberCode();
            // 将验证码存储到Redis，设置默认过期时间（这里使用VerificationCode工具类的默认过期时间逻辑，也可自己指定）
            VerificationCode.saveCode(key, code);
            return "验证码生成成功，已存储到Redis，验证码：" + code; // 实际场景可去掉明文显示，通过邮件/短信等告知用户
        } catch (Exception e) {
            return "生成并存储验证码失败：" + e.getMessage();
        }
    }

    /**
     * 校验Redis中存储的验证码
     * 访问方式：GET http://localhost:8090/redis/verifyCode?key=xxx&inputCode=xxx
     */
    @GetMapping("verifyCode")
    public String verifyCode(
            @RequestParam(required = true, value = "key") String key,
            @RequestParam(required = true, value = "inputCode") String inputCode
    ) {
        if (key.trim().isEmpty() || inputCode.trim().isEmpty()) {
            return "参数错误：key 和 inputCode 不能为空字符串";
        }
        try {
            boolean isVerified = VerificationCode.verifyCode(key, inputCode);
            if (isVerified) {
                return "验证码校验通过";
            } else {
                return "验证码校验失败，可能是验证码错误或已过期";
            }
        } catch (Exception e) {
            return "校验验证码失败：" + e.getMessage();
        }
    }

    /**
     * 向Redis中存储键值对
     * 访问方式：POST http://localhost:8090/redis/set?key=xxx&value=xxx
     */
    @PostMapping("set")
    public String setKey(
            @RequestParam(required = true, value = "key") String key,
            @RequestParam(required = true, value = "value") String value
    ) {
        if (key.trim().isEmpty() || value.trim().isEmpty()) {
            return "参数错误：key 和 value 不能为空字符串";
        }
        try {
            redisTemplate.opsForValue().set(key, value);
            return "成功存储：key=" + key + ", value=" + value;
        } catch (Exception e) {
            return "存储失败：" + e.getMessage();
        }
    }

    /**
     * 从Redis中获取值
     * 访问方式：GET http://localhost:8090/redis/get?key=xxx
     */
    @GetMapping("get")
    public String getKey(
            @RequestParam(required = true, value = "key") String key
    ) {
        if (key.trim().isEmpty()) {
            return "参数错误：key 不能为空字符串";
        }
        try {
            String value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                return "获取成功：key=" + key + ", value=" + value;
            } else {
                return "key不存在：" + key;
            }
        } catch (Exception e) {
            return "获取失败：" + e.getMessage();
        }
    }


    @DeleteMapping("delete")
    public String deleteKey(
            @RequestParam(required = true, value = "key") String key
    ) {
        if (key.trim().isEmpty()) {
            return "参数错误：key 不能为空字符串";
        }
        try {
            boolean success = redisTemplate.delete(key);
            return success ? "成功删除key：" + key : "删除失败，key不存在：" + key;
        } catch (Exception e) {
            return "删除失败：" + e.getMessage();
        }
    }
}