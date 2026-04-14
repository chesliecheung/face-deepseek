package edu.kust.facedeepseek.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.kust.facedeepseek.entity.Follow;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.kust.facedeepseek.entity.Follow;

public interface FollowService extends IService<Follow> {
    boolean isFollowing(Integer followerId, Integer followeeId);
}
