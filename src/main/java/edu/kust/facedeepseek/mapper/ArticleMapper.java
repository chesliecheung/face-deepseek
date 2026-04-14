package edu.kust.facedeepseek.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.kust.facedeepseek.entity.Article;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
}
