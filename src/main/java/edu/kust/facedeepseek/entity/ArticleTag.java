package edu.kust.facedeepseek.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
@TableName("article_tag")
public class ArticleTag implements Serializable {
    @TableId(value = "article_id")
    private Long articleId;

    @TableField("tag_id")
    private Integer tagId;
}
