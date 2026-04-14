package edu.kust.facedeepseek.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.kust.facedeepseek.service.CollectionArticleService;
import org.springframework.stereotype.Service;
import edu.kust.facedeepseek.mapper.CollectionArticleMapper;
import edu.kust.facedeepseek.entity.CollectionArticle;

@Service
public class CollectionArticleServiceImpl extends ServiceImpl<CollectionArticleMapper, CollectionArticle> implements CollectionArticleService {

    @Override
    public boolean isCollected(Long collectionId, Long articleId) {
        return this.lambdaQuery()
                .eq(CollectionArticle::getCollectionId, collectionId)
                .eq(CollectionArticle::getArticleId, articleId)
                .count() > 0;
    }
}
