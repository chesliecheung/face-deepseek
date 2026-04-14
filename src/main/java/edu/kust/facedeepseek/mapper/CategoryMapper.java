package edu.kust.facedeepseek.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.kust.facedeepseek.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}