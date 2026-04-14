package edu.kust.facedeepseek.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

/**
 * <p>
 * 用户表实体类，对应数据库中含 id、username、PASSWORD、email、imagetoken 字段的表
 * </p>
 */
@Data
@TableName("user") // 对应数据库表名，需与实际表名一致
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    // 主键，自增
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    // 对应数据库 username 字段
    @TableField("username")
    private String username;

    // 对应数据库 PASSWORD 字段，注意这里属性名小写，数据库字段大写不影响映射（MyBatis - Plus 会处理）
    @TableField("PASSWORD")
    private String password;

    // 对应数据库 email 字段
    @TableField("email")
    private String email;

    // 对应数据库 imagetoken 字段
    @TableField("imagetoken")
    private String imagetoken;

    @TableField("avatar_url")
    private String avatarUrl;

    @TableField("bio")
    private String bio;

}