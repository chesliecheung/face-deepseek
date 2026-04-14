package edu.kust.facedeepseek.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.kust.facedeepseek.entity.Follow;
import edu.kust.facedeepseek.mapper.FollowMapper;
import edu.kust.facedeepseek.service.FollowService;
import org.springframework.stereotype.Service;


@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {

    @Override
    public boolean isFollowing(Integer followerId, Integer followeeId) {
        return this.lambdaQuery()
                .eq(Follow::getFollowerId, followerId)
                .eq(Follow::getFolloweeId, followeeId)
                .count() > 0;
    }
}
