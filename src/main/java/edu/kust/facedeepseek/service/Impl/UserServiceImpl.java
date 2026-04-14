package edu.kust.facedeepseek.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.kust.facedeepseek.entity.User;
import edu.kust.facedeepseek.mapper.UserMapper;
import edu.kust.facedeepseek.service.UserService;
import org.springframework.stereotype.Service;
import edu.kust.facedeepseek.util.VerificationCode;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User loginByUsername(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        // 使用baseMapper的selectOne方法执行查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username)
                .eq("password", password);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public User loginByEmail(String email, String code) {
        if (email == null || code == null) {
            return null;
        }
        boolean valid = VerificationCode.verifyCode(email, code);
        if (!valid) return null;

        // 使用baseMapper的selectOne方法执行查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return baseMapper.selectOne(queryWrapper);


    }

    @Override
    public User loginByFaceToken(String faceToken) {
        if (faceToken == null) {
            return null;
        }
        // 使用baseMapper的selectOne方法执行查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("imagetoken", faceToken);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean checkUsernameExists(String username) {
        // 保持使用baseMapper的selectCount方法
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return baseMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean checkEmailExists(String email) {
        // 保持使用baseMapper的selectCount方法
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return baseMapper.selectCount(queryWrapper) > 0;
    }
}
