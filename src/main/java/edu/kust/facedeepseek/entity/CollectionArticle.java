package edu.kust.facedeepseek.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("collection_article") // 对应数据库表名
public class CollectionArticle {
    @TableId(type = IdType.AUTO) // 主键，指定自增策略
    private Long id;

    @TableField("collection_id") // 映射数据库字段 collection_id
    private Long collectionId;

    @TableField("article_id") // 映射数据库字段 article_id
    private Long articleId;
}