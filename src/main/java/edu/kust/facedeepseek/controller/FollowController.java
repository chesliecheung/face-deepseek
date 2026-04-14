
package edu.kust.facedeepseek.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.kust.facedeepseek.mapper.FollowMapper;
import edu.kust.facedeepseek.util.Result;
import edu.kust.facedeepseek.entity.Follow;
import edu.kust.facedeepseek.entity.User;
import edu.kust.facedeepseek.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.time.LocalDateTime;


@RestController
@RequestMapping("/follow")
public class FollowController {

    @Resource
    private FollowMapper followMapper;

    @PostMapping("/add")
    public Result<?> add(@RequestParam Integer followeeId,
                         @SessionAttribute("loginUser") User loginUser) {
        QueryWrapper<Follow> wrapper = new QueryWrapper<>();
        wrapper.eq("follower_id", loginUser.getId())
                .eq("followee_id", followeeId);

        if (followMapper.selectCount(wrapper) > 0) {
            return Result.error("已关注");
        }

        Follow follow = new Follow();
        follow.setFollowerId(loginUser.getId());
        follow.setFolloweeId(followeeId);
        follow.setCreateTime(LocalDateTime.now());
        followMapper.insert(follow);

        return Result.success();
    }

    @PostMapping("/remove")
    public Result<?> remove(@RequestParam Integer followeeId,
                            @SessionAttribute("loginUser") User loginUser) {
        QueryWrapper<Follow> wrapper = new QueryWrapper<>();
        wrapper.eq("follower_id", loginUser.getId())
                .eq("followee_id", followeeId);

        followMapper.delete(wrapper);
        return Result.success();
    }

    @GetMapping("/check")
    public Result<Boolean> checkFollowStatus(@RequestParam Integer followeeId,
                                             @SessionAttribute("loginUser") User loginUser) {
        QueryWrapper<Follow> wrapper = new QueryWrapper<>();
        wrapper.eq("follower_id", loginUser.getId())
                .eq("followee_id", followeeId);

        int count = followMapper.selectCount(wrapper);
        return Result.success(count > 0);
    }
}
