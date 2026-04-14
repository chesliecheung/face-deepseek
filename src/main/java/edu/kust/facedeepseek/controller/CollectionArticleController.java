package edu.kust.facedeepseek.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.kust.facedeepseek.entity.Collection;
import edu.kust.facedeepseek.entity.CollectionArticle;
import edu.kust.facedeepseek.entity.User;
import edu.kust.facedeepseek.mapper.CollectionArticleMapper;
import edu.kust.facedeepseek.mapper.CollectionMapper;
import edu.kust.facedeepseek.util.Result;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 文章与收藏夹的关联控制器（核心：实现「收藏文章」功能）
 */
@RestController
@RequestMapping("/collectionArticle")
public class CollectionArticleController {

    @Resource
    private CollectionArticleMapper collectionArticleMapper;
    @Resource
    private CollectionMapper collectionMapper; // 新增：注入CollectionMapper用于查询收藏夹

    @PostMapping("/add")
    public Result<?> addArticleToCollection(
            @RequestParam Long collectionId,
            @RequestParam Long articleId,
            @SessionAttribute("loginUser") User loginUser) {

        // 1. 基础参数校验（拦截null/非正数）
        if (collectionId == null || collectionId <= 0) {
            return Result.error("收藏夹ID无效");
        }
        if (articleId == null || articleId <= 0) {
            return Result.error("文章ID无效");
        }

        // 2. 校验收藏夹是否存在
        Collection collection = collectionMapper.selectById(collectionId);
        if (collection == null) {
            return Result.error("收藏夹不存在（可能已被删除）");
        }

        // 3. 校验收藏夹是否属于当前用户
        if (!collection.getUserId().equals(loginUser.getId())) {
            return Result.error("无权操作他人的收藏夹");
        }

        // 4. 校验是否已收藏（原有逻辑，保持不变）
        QueryWrapper<CollectionArticle> wrapper = new QueryWrapper<>();
        wrapper.eq("collection_id", collectionId)
                .eq("article_id", articleId);
        if (collectionArticleMapper.selectCount(wrapper) > 0) {
            return Result.error("该文章已在收藏夹中");
        }

        // 5. 插入关联记录（能执行到这里，说明参数全部合法）
        CollectionArticle ca = new CollectionArticle();
        ca.setCollectionId(collectionId);
        ca.setArticleId(articleId);
        collectionArticleMapper.insert(ca);

        return Result.success();
    }

    // 其他方法（remove、isCollected等）保持不变...
}