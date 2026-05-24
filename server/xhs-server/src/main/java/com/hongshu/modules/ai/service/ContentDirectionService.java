package com.hongshu.modules.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hongshu.modules.ai.entity.ContentDirection;
import com.hongshu.modules.ai.mapper.ContentDirectionMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentDirectionService {

    private final ContentDirectionMapper directionMapper;

    public ContentDirectionService(ContentDirectionMapper directionMapper) {
        this.directionMapper = directionMapper;
    }

    public List<ContentDirection> getAllDirections() {
        return directionMapper.selectList(new LambdaQueryWrapper<ContentDirection>()
                .orderByAsc(ContentDirection::getSortOrder));
    }

    public List<ContentDirection> getActiveDirections() {
        return directionMapper.selectList(new LambdaQueryWrapper<ContentDirection>()
                .eq(ContentDirection::getStatus, 1)
                .orderByAsc(ContentDirection::getSortOrder));
    }

    public ContentDirection createDirection(ContentDirection direction) {
        directionMapper.insert(direction);
        return direction;
    }

    public ContentDirection updateDirection(Long id, ContentDirection direction) {
        direction.setId(id);
        directionMapper.updateById(direction);
        return directionMapper.selectById(id);
    }

    public void deleteDirection(Long id) {
        directionMapper.deleteById(id);
    }
}
