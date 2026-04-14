package edu.kust.facedeepseek.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.kust.facedeepseek.entity.CollectionArticle;

public interface CollectionArticleService extends IService<CollectionArticle> {
    boolean isCollected(Long collectionId, Long articleId);
}
