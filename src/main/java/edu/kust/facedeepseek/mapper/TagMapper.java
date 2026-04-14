package edu.kust.facedeepseek.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.kust.facedeepseek.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {
}