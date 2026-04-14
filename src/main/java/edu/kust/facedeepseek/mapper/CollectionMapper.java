package edu.kust.facedeepseek.mapper;

import edu.kust.facedeepseek.entity.Collection;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import edu.kust.facedeepseek.entity.Collection;

@Mapper
public interface CollectionMapper extends BaseMapper<Collection> {
    // 例如：根据用户 ID 查收藏夹
    // List<Collection> selectByUserId(Integer userId);
}
