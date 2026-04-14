package edu.kust.facedeepseek.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.kust.facedeepseek.entity.*;
import edu.kust.facedeepseek.mapper.*;
import edu.kust.facedeepseek.service.ArticleService;
import edu.kust.facedeepseek.service.CategoryService;
import edu.kust.facedeepseek.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleController {



    @Resource
    private UserMapper userMapper;

    @Resource
    private ArticleLikeMapper articleLikeMapper; // 使用ArticleLikeMapper

    @Resource
    private CommentMapper commentMapper; // 假设有CommentMapper



    private final CategoryMapper categoryMapper; // 新增：分类Mapper


    private final ArticleService articleService;
    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;
    private final TagMapper tagMapper;
    private final FollowMapper followMapper;

    // 发布文章
    @PostMapping("/add")
    public Map<String, Object> addArticle(@RequestBody Article article, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        User currentUser = (User) session.getAttribute("loginUser");
        if (currentUser == null) {
            result.put("success", false);
            result.put("msg", "未登录");
            return result;
        }

        article.setUserId(currentUser.getId());
        if (article.getPublishTime() == null) {
            article.setPublishTime(LocalDateTime.now());
        }
        if (article.getStatus() == null) {
            article.setStatus("published");
        }

        boolean saved = articleService.save(article);
        result.put("success", saved);
        result.put("data", article);
        return result;
    }

    // 保存草稿
    @PostMapping("/draft")
    public Map<String, Object> saveDraft(@RequestBody Article article, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        User currentUser = (User) session.getAttribute("loginUser");
        if (currentUser == null) {
            result.put("success", false);
            result.put("msg", "未登录");
            return result;
        }

        article.setUserId(currentUser.getId());
        article.setStatus("draft");
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());

        boolean saved = articleService.save(article);

        if (saved) {
            result.put("success", true);
            result.put("msg", "草稿保存成功");
            result.put("data", article);
        } else {
            result.put("success", false);
            result.put("msg", "草稿保存失败");
        }

        return result;
    }



    @PostMapping("/publish")
    public Map<String, Object> publishArticle(@RequestBody Article article, HttpSession session) {
        System.out.println("后端最终接收的封面URL：" + article.getCoverImageUrl());
        Map<String, Object> result = new HashMap<>();

        // 1. 登录校验
        User currentUser = (User) session.getAttribute("loginUser");
        if (currentUser == null) {
            result.put("success", false);
            result.put("msg", "未登录");
            return result;
        }

        // 2. 文章基础信息设置
        article.setUserId(currentUser.getId());
        article.setStatus("published");
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());

        // 封面默认值
        if (article.getCoverImageUrl() == null) {
            article.setCoverImageUrl("http://localhost:9999/uploads/default-cover.png");
        }

        if (article.getPublishTime() == null) {
            article.setPublishTime(LocalDateTime.now());
        }

        // 3. 保存文章主体，获取文章ID
        boolean saved = articleService.save(article);
        Long articleId = article.getId(); // 保存后自动生成的文章ID
        if (!saved) {
            result.put("success", false);
            result.put("msg", "文章发布失败");
            return result;
        }

        // 4. 处理「分类关联」
        Integer categoryId = article.getCategoryId();
        if (categoryId != null) {
            QueryWrapper<Category> categoryQw = new QueryWrapper<>();
            categoryQw.eq("id", categoryId);
            Category existingCategory = categoryMapper.selectOne(categoryQw);
            if (existingCategory == null) {
                result.put("success", false);
                result.put("msg", "选择的分类不存在，请重新选择");
                return result;
            }
        }

        // 5. 处理「标签自动创建 & 关联」
        // 新增：同时收集所有标签名，用于写回 article.tags 字段
        List<String> tagNamesList = new ArrayList<>();
        if (article.getTags() != null && !article.getTags().isEmpty()) {
            for (String tagName : article.getTags()) {
                tagName = tagName.trim();
                if (tagName.isEmpty()) continue;

                // 检查标签是否存在
                QueryWrapper<Tag> tagQw = new QueryWrapper<>();
                tagQw.eq("name", tagName);
                Tag existingTag = tagMapper.selectOne(tagQw);

                Tag tag = existingTag;
                if (existingTag == null) {
                    tag = new Tag();
                    tag.setName(tagName);
                    tagMapper.insert(tag);
                }

                // 关联文章与标签（插入中间表）
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(articleId);
                articleTag.setTagId(tag.getId());
                articleTagMapper.insert(articleTag);

                tagNamesList.add(tag.getName());
            }

            // 写回 article 表 tags 字段（逗号分隔）
            article.setTags(tagNamesList);
            articleMapper.updateById(article);
        }

        // 6. 发布成功
        result.put("success", true);
        result.put("msg", "文章发布成功");
        result.put("data", article);
        return result;
    }




    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(defaultValue = "") String keyword) {
        Page<Article> pg = new Page<>(page, size);
        QueryWrapper<Article> qw = new QueryWrapper<>();
        qw.eq("status", "published")
                .eq("visibility", "public")
                .orderByDesc("publish_time");

        if (!keyword.isEmpty()) {
            qw.like("title", keyword);
        }

        IPage<Article> r = articleMapper.selectPage(pg, qw);

        // 关键改动：返回自定义的数据列表（带作者名）
        List<Map<String, Object>> records = r.getRecords().stream().map(article -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", article.getId());
            map.put("title", article.getTitle());
            map.put("summary", article.getSummary());
            map.put("coverImageUrl", article.getCoverImageUrl());
            map.put("publishTime", article.getPublishTime());

            // 查询作者
            User user = userMapper.selectById(article.getUserId());
            map.put("username", user != null ? user.getUsername() : null);

            return map;
        }).collect(Collectors.toList());

        // 构造新的分页结果
        Map<String, Object> data = new HashMap<>();
        data.put("records", records);
        data.put("current", r.getCurrent());
        data.put("pages", r.getPages());
        data.put("total", r.getTotal());

        return Result.success("ok", data);
    }



    // 详情（带可见性校验）
    @GetMapping("/detail/{id}")
    public Result<?> detail(@PathVariable Long id, HttpSession session) {
        Article a = articleMapper.selectById(id);
        if (a == null) return Result.fail("文章不存在");


        // ===== 新增：阅读量+1 =====
        a.setReadCount(a.getReadCount() + 1);
        articleMapper.updateById(a); // 保存更新后的阅读量

        // 校验可见性
        if (!"public".equals(a.getVisibility())) {
            User loginUser = (User) session.getAttribute("loginUser");
            if (loginUser == null) return Result.fail("未登录或无权限");

            if (a.getUserId().equals(loginUser.getId())) {
                // 作者自己可以看
            } else if ("followers".equals(a.getVisibility())) {
                Integer count = followMapper.selectCount(new QueryWrapper<Follow>()
                        .eq("follower_id", loginUser.getId())
                        .eq("followee_id", a.getUserId()));
                if (count == null || count == 0) return Result.fail("仅限粉丝可见");
            } else {
                return Result.fail("仅作者可见");
            }
        }

        // 获取标签
        List<ArticleTag> atList = articleTagMapper.selectList(
                new QueryWrapper<ArticleTag>().eq("article_id", id));
        List<Integer> tagIds = atList.stream().map(ArticleTag::getTagId).collect(Collectors.toList());
        List<Tag> tags = tagIds.isEmpty() ? Collections.emptyList() : tagMapper.selectBatchIds(tagIds);

        Map<String, Object> data = new HashMap<>();
        data.put("article", a);
        data.put("tags", tags);

        return Result.success("ok", data);
    }


    @PostMapping("/uploadCover")
    public Map<String, Object> uploadCover(@RequestParam("file") MultipartFile file, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        // 1. 先校验登录状态（已有逻辑保留）
        User currentUser = (User) session.getAttribute("loginUser");
        if (currentUser == null) {
            result.put("success", false);
            result.put("msg", "未登录");
            return result;
        }

        try {
            // 2. 校验文件是否为空（已有逻辑保留）
            if (file.isEmpty()) {
                result.put("success", false);
                result.put("msg", "文件为空");
                return result;
            }

            // 3. 保存图片到服务器（已有逻辑保留）
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            // 注意：这里建议用项目的绝对路径，避免路径问题（示例：D:/project/uploads/ 或 /var/project/uploads/）
            Path filePath = Paths.get("uploads", fileName);
            Files.createDirectories(filePath.getParent()); // 确保文件夹存在
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 4. 拼接可访问的图片 URL（已有逻辑保留）
            String url = "/uploads/" + fileName;

            // 5. 关键修改：只返回 URL，不创建新的 Article 对象！
            // （删除原来的 Article article = new Article(); 和 articleService.save(article); 这两行）
            result.put("success", true);
            result.put("url", url); // 只返回封面 URL 给前端

        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "封面上传失败：" + e.getMessage());
        }

        return result;
    }




    // ======= 在 ArticleController 类中添加以下方法 =======

    /**
     * 与前端 detail.html 对接的接口（通过 query param id）。
     * 返回结构为：res.data -> Map 包含 article 基本字段 + username + tags + 预留的统计/状态字段
     */
    @GetMapping("/detail")
    public Result<?> detailByParam(@RequestParam Long id, HttpSession session) {
        // 与现有 /detail/{id} 的可见性逻辑保持一致
        Article a = articleMapper.selectById(id);
        if (a == null) {
            return Result.fail("文章不存在");
        }

        // ===== 新增：阅读量+1 =====
        a.setReadCount(a.getReadCount() + 1);
        articleMapper.updateById(a);

        // 可见性校验（沿用你已有逻辑）
        if (!"public".equals(a.getVisibility())) {
            User loginUser = (User) session.getAttribute("loginUser");
            if (loginUser == null) return Result.fail("未登录或无权限");

            if (a.getUserId().equals(loginUser.getId())) {
                // 作者自己可以看
            } else if ("followers".equals(a.getVisibility())) {
                Integer count = followMapper.selectCount(new QueryWrapper<Follow>()
                        .eq("follower_id", loginUser.getId())
                        .eq("followee_id", a.getUserId()));
                if (count == null || count == 0) return Result.fail("仅限粉丝可见");
            } else {
                return Result.fail("仅作者可见");
            }
        }

        // 获取标签
        List<ArticleTag> atList = articleTagMapper.selectList(
                new QueryWrapper<ArticleTag>().eq("article_id", id));
        List<Integer> tagIds = atList.stream().map(ArticleTag::getTagId).collect(Collectors.toList());
        List<Tag> tags = tagIds.isEmpty() ? Collections.emptyList() : tagMapper.selectBatchIds(tagIds);

        // 获取作者信息
        User author = userMapper.selectById(a.getUserId());

        // 构造返回的 article map（扁平化，方便前端直接使用 res.data.xxx）
        Map<String, Object> articleMap = new HashMap<>();
        articleMap.put("id", a.getId());
        articleMap.put("title", a.getTitle());
        articleMap.put("content", a.getContent()); // html 或 markdown，前端直接 innerHTML
        articleMap.put("summary", a.getSummary());
        articleMap.put("categoryId", a.getCategoryId());
        articleMap.put("visibility", a.getVisibility());
        articleMap.put("coverImageUrl", a.getCoverImageUrl());
        articleMap.put("status", a.getStatus());
        articleMap.put("publishTime", a.getPublishTime());
        articleMap.put("createTime", a.getCreateTime());
        articleMap.put("updateTime", a.getUpdateTime());

        // 作者相关
        // 作者相关
        articleMap.put("authorId", a.getUserId());
        articleMap.put("username", author != null ? author.getUsername() : null);
        articleMap.put("authorAvatar", author != null ? author.getAvatarUrl() : null);

        // 标签
        List<String> tagNames = tags.stream().map(Tag::getName).collect(Collectors.toList());
        articleMap.put("tags", tagNames);

        // 预留统计/状态字段（当前使用占位值，后续可接入真实表）
        articleMap.put("likeCount", 0);         // 点赞数
        articleMap.put("favCount", 0);          // 收藏数
        articleMap.put("commentCount", 0);      // 评论数（若后续有 comment 表，可替换）
        articleMap.put("isLiked", false);       // 当前用户是否已点赞
        articleMap.put("isFavorited", false);   // 当前用户是否已收藏
        articleMap.put("isFollowing", false);   // 当前用户是否关注作者

        // 如果需要，根据 session 填充 isLiked/isFavorited/isFollowing（若你已有相应表可添加查询）
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser != null) {
            Integer uid = loginUser.getId();
            // 例：如果你将来有 article_like 表可以解开并查询：
            // Integer likeCountForUser = articleLikeMapper.selectCount(new QueryWrapper<ArticleLike>().eq("article_id", id).eq("user_id", uid));
            // articleMap.put("isLiked", likeCountForUser != null && likeCountForUser > 0);
            // 同理 fav 和 follow
        }

        return Result.success("ok", articleMap);
    }

    /**
     * 点赞开关（占位 stub）
     * 前端可以 POST /article/like/toggle 传 { articleId: .. }
     * 这里目前返回占位 success=false（未实现），方便前端先接入并显示友好提示或动画
     * 后续你可以根据有无 like 表实现实际逻辑（插入/删除 + 返回新的 likeCount）
     */
    @PostMapping("/like/toggle")
    public Result<?> toggleLike(@RequestParam Long articleId, HttpSession session) {
        User u = (User) session.getAttribute("loginUser");
        if (u == null) return Result.fail("未登录");

        // TODO: 实现点赞逻辑（检查 article_like 表，插入或删除，并返回最新计数）
        Map<String, Object> data = new HashMap<>();
        data.put("implemented", false);
        data.put("message", "点赞接口未实现（后端占位），请实现 article_like 表及对应逻辑");
        return Result.success("not-implemented", data);
    }

    /**
     * 收藏开关（占位 stub）
     * POST /article/favorite/toggle 传 { articleId: .. }
     */
    @PostMapping("/favorite/toggle")
    public Result<?> toggleFavorite(@RequestParam Long articleId, HttpSession session) {
        User u = (User) session.getAttribute("loginUser");
        if (u == null) return Result.fail("未登录");

        // TODO: 实现收藏逻辑（article_favorite 表）
        Map<String, Object> data = new HashMap<>();
        data.put("implemented", false);
        data.put("message", "收藏接口未实现（后端占位）");
        return Result.success("not-implemented", data);
    }

    /**
     * 关注/取消关注作者（占位 stub）
     * POST /article/follow/toggle 传 { followeeId: .. }
     */
    @PostMapping("/follow/toggle")
    public Result<?> toggleFollow(@RequestParam Integer followeeId, HttpSession session) {
        User u = (User) session.getAttribute("loginUser");
        if (u == null) return Result.fail("未登录");

        // TODO: 实现 follow 表插入/删除逻辑
        Map<String, Object> data = new HashMap<>();
        data.put("implemented", false);
        data.put("message", "关注接口未实现（后端占位）");
        return Result.success("not-implemented", data);
    }


    /**
     * 新增：获取当前用户的文章列表（前端个人信息页调用：/article/my）
     * 按用户ID查询，包含已发布和草稿
     */
    @GetMapping("/my")
    public Result<?> getMyArticles(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.fail("未登录");
        }
        // 按用户ID查询，包含已发布（published）和草稿（draft），按更新时间倒序
        QueryWrapper<Article> qw = new QueryWrapper<>();
        qw.eq("user_id", loginUser.getId())
                .in("status", "published", "draft")
                .orderByDesc("update_time");

        List<Article> myArticles = articleMapper.selectList(qw);
        // 拼接作者信息和标签名（前端需要显示）
        List<Map<String, Object>> resultList = myArticles.stream().map(article -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", article.getId()); // 文章ID（用于跳转/删除）
            map.put("title", article.getTitle()); // 文章标题
            map.put("summary", article.getSummary()); // 摘要
            map.put("status", article.getStatus()); // 状态（published/draft）
            map.put("publishTime", article.getPublishTime()); // 发布时间
            map.put("updateTime", article.getUpdateTime()); // 更新时间
            // 标签（可选，前端如需显示）
            List<ArticleTag> atList = articleTagMapper.selectList(
                    new QueryWrapper<ArticleTag>().eq("article_id", article.getId())
            );
            List<String> tagNames = atList.stream()
                    .map(at -> tagMapper.selectById(at.getTagId()).getName())
                    .collect(Collectors.toList());
            map.put("tags", tagNames);
            return map;
        }).collect(Collectors.toList());

        return Result.success("获取个人文章成功", resultList);
    }

    /**
     * 新增：删除个人文章（前端调用：/article/delete）
     */
    @PostMapping("/delete")
    public Result<?> deleteArticle(@RequestParam Long articleId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.fail("未登录");
        }
        // 校验文章归属（只能删除自己的文章）
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            return Result.fail("文章不存在");
        }
        if (!article.getUserId().equals(loginUser.getId())) {
            return Result.fail("无权删除他人文章");
        }
        // 先删除关联的文章标签（article_tag），再删除文章
        articleTagMapper.delete(new QueryWrapper<ArticleTag>().eq("article_id", articleId));
        articleMapper.deleteById(articleId);
        return Result.success("文章删除成功");
    }




// 在ArticleController类中添加以下接口
// 在ArticleController类中添加以下接口

    /**
     * 获取用户文章统计数据
     */
    @GetMapping("/stats")
    public Result<?> getArticleStats(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.fail("未登录");
        }

        // 获取用户的所有文章
        QueryWrapper<Article> qw = new QueryWrapper<>();
        qw.eq("user_id", loginUser.getId());
        List<Article> articles = articleMapper.selectList(qw);

        // 统计文章状态
        long publishedCount = articles.stream().filter(a -> "published".equals(a.getStatus())).count();
        long draftCount = articles.stream().filter(a -> "draft".equals(a.getStatus())).count();

        // 统计月度发布趋势（按月份统计发布的文章数）
        Map<String, Long> monthlyTrend = new LinkedHashMap<>();
        // 初始化12个月
        for (int i = 1; i <= 12; i++) {
            monthlyTrend.put(i + "月", 0L);
        }

        articles.stream()
                .filter(a -> a.getPublishTime() != null && "published".equals(a.getStatus()))
                .forEach(a -> {
                    LocalDateTime publishTime = a.getPublishTime();
                    String monthKey = publishTime.getMonthValue() + "月";
                    monthlyTrend.put(monthKey, monthlyTrend.get(monthKey) + 1);
                });

        // 获取文章互动数据（从点赞和评论表中获取）
        long totalLikes = 0;
        long totalComments = 0;

        for (Article article : articles) {
            // 获取每篇文章的点赞数
            QueryWrapper<ArticleLike> likeQw = new QueryWrapper<>();
            likeQw.eq("article_id", article.getId());
            totalLikes += articleLikeMapper.selectCount(likeQw);

            // 获取每篇文章的评论数
            QueryWrapper<Comment> commentQw = new QueryWrapper<>();
            commentQw.eq("article_id", article.getId());
            totalComments += commentMapper.selectCount(commentQw);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("publishedCount", publishedCount);
        data.put("draftCount", draftCount);
        data.put("monthlyTrend", monthlyTrend);
        data.put("totalLikes", totalLikes);
        data.put("totalComments", totalComments);

        return Result.success("获取文章统计数据成功", data);
    }

    /**
     * 获取文章互动数据（每篇文章的点赞和评论数）
     */
    @GetMapping("/interaction")
    public Result<?> getArticleInteraction(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.fail("未登录");
        }

        // 获取用户的所有文章
        QueryWrapper<Article> qw = new QueryWrapper<>();
        qw.eq("user_id", loginUser.getId())
                .eq("status", "published");
        List<Article> articles = articleMapper.selectList(qw);

        List<Map<String, Object>> interactionList = new ArrayList<>();

        for (Article article : articles) {
            Long articleId = article.getId();

            // 获取点赞数
            QueryWrapper<ArticleLike> likeQw = new QueryWrapper<>();
            likeQw.eq("article_id", articleId);
            Integer likeCount = articleLikeMapper.selectCount(likeQw);

            // 获取评论数
            QueryWrapper<Comment> commentQw = new QueryWrapper<>();
            commentQw.eq("article_id", articleId);
            Integer commentCount = commentMapper.selectCount(commentQw);

            Map<String, Object> map = new HashMap<>();
            map.put("articleId", articleId);
            map.put("title", article.getTitle());
            map.put("likeCount", likeCount);
            map.put("commentCount", commentCount);

            interactionList.add(map);
        }

        return Result.success("获取文章互动数据成功", interactionList);
    }

    /**
     * 获取用户的热门标签数据
     */
    @GetMapping("/popularTags")
    public Result<?> getPopularTags(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.fail("未登录");
        }

        // 获取用户的所有文章
        QueryWrapper<Article> qw = new QueryWrapper<>();
        qw.eq("user_id", loginUser.getId());
        List<Article> articles = articleMapper.selectList(qw);

        // 提取所有标签并统计
        Map<String, Long> tagCountMap = new HashMap<>();
        for (Article article : articles) {
            if (article.getTags() != null) {
                for (String tag : article.getTags()) {
                    tagCountMap.put(tag, tagCountMap.getOrDefault(tag, 0L) + 1);
                }
            }
        }

        // 排序并取前5个
        List<Map.Entry<String, Long>> sortedTags = tagCountMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        // 转换为前端需要的格式
        List<String> tagNames = sortedTags.stream().map(Map.Entry::getKey).collect(Collectors.toList());
        List<Long> tagCounts = sortedTags.stream().map(Map.Entry::getValue).collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("tagNames", tagNames);
        data.put("tagCounts", tagCounts);
        data.put("totalTags", tagCountMap.size());

        return Result.success("获取热门标签成功", data);
    }



    /**
     * 获取作者发布的文章列表
     * GET /article/listByAuthor
     */
    @GetMapping("/listByAuthor")
    public Result<?> listByAuthor(@RequestParam Integer authorId,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        Page<Article> pg = new Page<>(page, size);
        QueryWrapper<Article> qw = new QueryWrapper<>();
        qw.eq("user_id", authorId)
                .eq("status", "published")
                .orderByDesc("publish_time");

        IPage<Article> r = articleMapper.selectPage(pg, qw);

        // 获取文章标签、点赞数、评论数
        List<Map<String, Object>> records = r.getRecords().stream().map(article -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", article.getId());
            map.put("title", article.getTitle());
            map.put("summary", article.getSummary());
            map.put("coverImageUrl", article.getCoverImageUrl());
            map.put("publishTime", article.getPublishTime());

            // 点赞数
            QueryWrapper<ArticleLike> likeQw = new QueryWrapper<>();
            likeQw.eq("article_id", article.getId());
            Integer likeCount = articleLikeMapper.selectCount(likeQw);
            map.put("likeCount", likeCount);

            // 评论数
            QueryWrapper<Comment> commentQw = new QueryWrapper<>();
            commentQw.eq("article_id", article.getId());
            Integer commentCount = commentMapper.selectCount(commentQw);
            map.put("commentCount", commentCount);

            // 标签
            List<ArticleTag> atList = articleTagMapper.selectList(
                    new QueryWrapper<ArticleTag>().eq("article_id", article.getId()));
            List<Integer> tagIds = atList.stream().map(ArticleTag::getTagId).collect(Collectors.toList());
            List<Tag> tags = tagIds.isEmpty() ? Collections.emptyList() : tagMapper.selectBatchIds(tagIds);
            List<String> tagNames = tags.stream().map(Tag::getName).collect(Collectors.toList());
            map.put("tags", tagNames);

            return map;
        }).collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("records", records);
        data.put("current", r.getCurrent());
        data.put("pages", r.getPages());
        data.put("total", r.getTotal());

        return Result.success("ok", data);
    }





}
