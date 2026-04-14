package edu.kust.facedeepseek.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.kust.facedeepseek.entity.ArticleLike;
import edu.kust.facedeepseek.entity.User;
import edu.kust.facedeepseek.mapper.ArticleLikeMapper;
import edu.kust.facedeepseek.util.Result;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/like")
public class LikeController {

    @Resource
    private ArticleLikeMapper likeMapper;

    @PostMapping("/add")
    public Result<?> add(@RequestParam Long articleId,
                         @SessionAttribute("loginUser") User loginUser) {
        QueryWrapper<ArticleLike> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", loginUser.getId())
                .eq("article_id", articleId);

        if (likeMapper.selectCount(wrapper) > 0) {
            return Result.error("已点赞");
        }

        ArticleLike like = new ArticleLike();
        like.setArticleId(articleId);
        like.setUserId(loginUser.getId());
        like.setCreateTime(LocalDateTime.now());
        likeMapper.insert(like);

        return Result.success();
    }

    @PostMapping("/remove")
    public Result<?> remove(@RequestParam Long articleId,
                            @SessionAttribute("loginUser") User loginUser) {
        QueryWrapper<ArticleLike> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", loginUser.getId())
                .eq("article_id", articleId);

        likeMapper.delete(wrapper);
        return Result.success();
    }

    @GetMapping("/count")
    public Result<Integer> count(@RequestParam Long articleId) {
        QueryWrapper<ArticleLike> wrapper = new QueryWrapper<>();
        wrapper.eq("article_id", articleId);
        return Result.success(likeMapper.selectCount(wrapper).intValue());
    }

    @GetMapping("/check")
    public Result<Boolean> checkLikeStatus(@RequestParam Long articleId,
                                           @SessionAttribute("loginUser") User loginUser) {
        QueryWrapper<ArticleLike> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", loginUser.getId())
                .eq("article_id", articleId);

        int count = likeMapper.selectCount(wrapper);
        return Result.success(count > 0);
    }
}

