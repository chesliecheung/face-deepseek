package edu.kust.facedeepseek.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.kust.facedeepseek.entity.Comment;
import org.apache.ibatis.annotations.Mapper;



@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    // 这里可以保留你自定义的方法
}
