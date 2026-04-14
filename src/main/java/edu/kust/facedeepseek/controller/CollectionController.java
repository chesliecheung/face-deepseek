

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
import java.util.List;


@RestController
@RequestMapping("/collection")
public class CollectionController {

    @Resource
    private CollectionMapper collectionMapper;

    @Resource
    private CollectionArticleMapper collectionArticleMapper;

    @PostMapping("/add")
    public Result<?> add(@RequestParam String name,
                         @SessionAttribute("loginUser") User loginUser) {
        QueryWrapper<Collection> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", loginUser.getId())
                .eq("name", name);

        if (collectionMapper.selectCount(wrapper) > 0) {
            return Result.error("该收藏夹已存在");
        }

        Collection c = new Collection();
        c.setUserId(loginUser.getId());
        c.setName(name);
        collectionMapper.insert(c);

        // 关键修改：返回新创建的收藏夹ID，前端需要用这个ID关联文章
        return Result.success(c.getId());
    }

    @PostMapping("/remove")
    public Result<?> remove(@RequestParam Long id,
                            @SessionAttribute("loginUser") User loginUser) {
        QueryWrapper<Collection> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id)
                .eq("user_id", loginUser.getId());

        collectionMapper.delete(wrapper);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<?> list(@SessionAttribute("loginUser") User loginUser) {
        QueryWrapper<Collection> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", loginUser.getId());
        return Result.success(collectionMapper.selectList(wrapper));
    }


    @PostMapping("/article/add")
    public Result<?> addArticleToCollection(@RequestParam Long collectionId,
                                            @RequestParam Long articleId,
                                            @SessionAttribute("loginUser") User loginUser) {
        // 检查收藏夹是否属于当前用户
        Collection collection = collectionMapper.selectById(collectionId);
        if (collection == null || !collection.getUserId().equals(loginUser.getId())) {
            return Result.error("收藏夹不存在或无权操作");
        }

        // 检查是否已经收藏
        QueryWrapper<CollectionArticle> wrapper = new QueryWrapper<>();
        wrapper.eq("collection_id", collectionId)
                .eq("article_id", articleId);

        if (collectionArticleMapper.selectCount(wrapper) > 0) {
            return Result.error("已收藏");
        }

        CollectionArticle ca = new CollectionArticle();
        ca.setCollectionId(collectionId);
        ca.setArticleId(articleId);
        collectionArticleMapper.insert(ca);

        return Result.success();
    }


    /**
     * 新增：检查文章是否已被当前用户收藏
     * 前端调用路径：/collection/article/isCollected?articleId=xxx
     */
    @GetMapping("/article/isCollected")
    public Result<?> isArticleCollected(
            @RequestParam Long articleId,
            @SessionAttribute("loginUser") User loginUser) {

        // 1. 查询当前用户的所有收藏夹ID
        QueryWrapper<Collection> collectionWrapper = new QueryWrapper<>();
        collectionWrapper.eq("user_id", loginUser.getId());
        List<Collection> userCollections = collectionMapper.selectList(collectionWrapper);
        if (userCollections.isEmpty()) {
            // 没有收藏夹，直接返回未收藏
            return Result.success(false);
        }

        // 2. 提取收藏夹ID列表
        List<Long> collectionIds = userCollections.stream()
                .map(Collection::getId)
                .toList();

        // 3. 检查这些收藏夹中是否包含目标文章
        QueryWrapper<CollectionArticle> caWrapper = new QueryWrapper<>();
        caWrapper.in("collection_id", collectionIds)  // 属于用户的收藏夹
                .eq("article_id", articleId);         // 包含目标文章

        long count = collectionArticleMapper.selectCount(caWrapper);
        return Result.success(count > 0);  // true=已收藏，false=未收藏
    }




}
