package edu.kust.facedeepseek.service;


import com.baomidou.mybatisplus.extension.service.IService;

import edu.kust.facedeepseek.entity.Tag;

public interface TagService extends IService<Tag> {

    // 根据标签名称查询标签
    Tag getByName(String tagName);
}
