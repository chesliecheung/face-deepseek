package edu.kust.facedeepseek.service.Impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.kust.facedeepseek.service.CollectionService;
import org.springframework.stereotype.Service;
import edu.kust.facedeepseek.mapper.CollectionMapper;
import edu.kust.facedeepseek.entity.Collection;

@Service
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection> implements CollectionService {
}
