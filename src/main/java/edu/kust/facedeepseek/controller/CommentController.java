package edu.kust.facedeepseek.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.kust.facedeepseek.mapper.UserMapper;
import edu.kust.facedeepseek.util.Result;
import edu.kust.facedeepseek.entity.Article;
import edu.kust.facedeepseek.entity.Comment;
import edu.kust.facedeepseek.entity.User;
import edu.kust.facedeepseek.mapper.ArticleMapper;
import edu.kust.facedeepseek.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;


    @PostMapping("/add")
    public Result<?> add(@RequestBody Comment comment, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return Result.fail("未登录");

        Article article = articleMapper.selectById(comment.getArticleId());
        if (article == null) return Result.fail("文章不存在");

        comment.setUserId(loginUser.getId());
        comment.setCreateTime(LocalDateTime.now());
        comment.setStatus("normal");
        commentService.save(comment);

        return Result.success("评论成功", comment);
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam Long articleId) {
        List<Comment> comments = commentService.list(
                new QueryWrapper<Comment>()
                        .eq("article_id", articleId)
                        .orderByAsc("create_time")
        );

        List<Map<String, Object>> result = comments.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("articleId", c.getArticleId());
            map.put("content", c.getContent());
            map.put("createTime", c.getCreateTime());

            // ⭐ 关键：查用户信息
            User user = userMapper.selectById(c.getUserId());
            if (user != null) {
                map.put("username", user.getUsername());
                map.put("userAvatar", user.getAvatarUrl());
            } else {
                map.put("username", "未知用户");
                map.put("userAvatar", null);
            }

            return map;
        }).collect(Collectors.toList());

        return Result.success("ok", result);
    }

}
