package edu.kust.facedeepseek.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.kust.facedeepseek.entity.Tag;
import edu.kust.facedeepseek.mapper.TagMapper;
import edu.kust.facedeepseek.service.TagService;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    @Override
    public Tag getByName(String tagName) {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", tagName);
        return baseMapper.selectOne(queryWrapper);
    }
}