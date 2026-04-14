package edu.kust.facedeepseek.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.kust.facedeepseek.entity.Comment;
import edu.kust.facedeepseek.mapper.CommentMapper;
import edu.kust.facedeepseek.service.CommentService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
}
