package edu.kust.facedeepseek.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.kust.facedeepseek.entity.User;
import java.util.List;


public interface UserService extends IService<User> {

    User loginByUsername(String username, String password);

    // 邮箱验证码登录：验证邮箱和验证码
    User loginByEmail(String email, String code);

    // 人脸登录：通过faceToken查询用户
    User loginByFaceToken(String faceToken);

    // 检查用户名是否已存在
    boolean checkUsernameExists(String username);

    // 检查邮箱是否已存在
    boolean checkEmailExists(String email);


}
