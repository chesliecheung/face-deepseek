package edu.kust.facedeepseek.service.Impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.kust.facedeepseek.entity.Category;
import edu.kust.facedeepseek.mapper.CategoryMapper;
import edu.kust.facedeepseek.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    // 无需手动注入 mapper，ServiceImpl 已提供 baseMapper 属性

    @Override
    public boolean existsById(Long categoryId) {
        // 调用 baseMapper 检查分类是否存在
        return baseMapper.selectById(categoryId) != null;
    }
}