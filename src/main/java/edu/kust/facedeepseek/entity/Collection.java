package edu.kust.facedeepseek.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("collection") // 指定对应的数据库表名
public class Collection {
    @TableId(type = IdType.AUTO) // 主键注解，指定自增策略
    private Long id;

    @TableField("user_id") // 数据库字段名，如果与属性名一致可以省略
    private Integer userId;  // 用户ID

    @TableField("name") // 数据库字段名，如果与属性名一致可以省略
    private String name;     // 收藏夹名称
}