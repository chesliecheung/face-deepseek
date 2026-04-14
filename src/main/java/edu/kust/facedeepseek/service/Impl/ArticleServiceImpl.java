package edu.kust.facedeepseek.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.kust.facedeepseek.entity.Article;
import edu.kust.facedeepseek.mapper.ArticleMapper;
import edu.kust.facedeepseek.service.ArticleService;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
}
