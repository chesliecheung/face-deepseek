package edu.kust.facedeepseek.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.kust.facedeepseek.entity.Category;

// 继承 MyBatis-Plus 的 IService，获得基础 CRUD 能力
public interface CategoryService extends IService<Category> {
    // 可自定义业务方法，例如：
    // 检查分类是否存在
    boolean existsById(Long categoryId);
}