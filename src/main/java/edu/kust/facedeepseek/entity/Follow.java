package edu.kust.facedeepseek.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("follow")
public class Follow implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("follower_id")
    private Integer followerId;

    @TableField("followee_id")
    private Integer followeeId;

    @TableField("create_time")
    private LocalDateTime createTime;
}
