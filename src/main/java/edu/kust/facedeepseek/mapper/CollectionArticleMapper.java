package edu.kust.facedeepseek.mapper;
import edu.kust.facedeepseek.entity.CollectionArticle;
import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import edu.kust.facedeepseek.entity.CollectionArticle;

@Mapper
public interface CollectionArticleMapper extends BaseMapper<CollectionArticle> {
    int delete(Long collectionId, Long articleId);
    boolean exists(Long collectionId, Long articleId);
}
