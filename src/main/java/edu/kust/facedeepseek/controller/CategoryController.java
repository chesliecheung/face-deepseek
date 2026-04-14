package edu.kust.facedeepseek.controller;

import edu.kust.facedeepseek.entity.Category;
import edu.kust.facedeepseek.mapper.CategoryMapper;
import edu.kust.facedeepseek.service.CategoryService;
import edu.kust.facedeepseek.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    // 初始化分类的接口（仅测试或首次启动时调用）
    @GetMapping("/init")
    public Result<?> initCategories() {
        List<String> categoryNames = Arrays.asList("技术", "生活", "旅行", "美食", "健康");
        for (String name : categoryNames) {
            Category category = new Category();
            category.setName(name);
            category.setParentId(0L); // 一级分类
            category.setCreatedAt(LocalDateTime.now());
            categoryService.save(category);
        }
        return Result.success("分类初始化成功");
    }

    // 分类列表接口（供前端渲染选择器）
    @GetMapping("/list")
    public Result<?> listCategories() {
        List<Category> categories = categoryService.list();
        return Result.success("分类列表", categories);
    }
}