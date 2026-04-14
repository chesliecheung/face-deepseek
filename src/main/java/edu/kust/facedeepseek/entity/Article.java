package edu.kust.facedeepseek.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("article")
public class Article implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Integer userId;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("summary")
    private String summary;

    @TableField("category_id")
    private Integer categoryId;

    @TableField("visibility")
    private String visibility;

    @TableField("comment_permission")
    private String commentPermission;

    @TableField("cover_image_url")
    private String coverImageUrl;

    @TableField("status")
    private String status;

    @TableField("publish_time")
    private LocalDateTime publishTime;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;


    // 新增：阅读量（默认0，每次访问文章详情页+1）
    @TableField("read_count")
    private Integer readCount = 0; // 数据库需同步新增 `read_count` 字段（INT类型，默认0）



    // 非数据库字段：前端传过来的标签列表
    @TableField(exist = false)
    private List<String> tags;


}
