package com.hongshu.modules.content.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hongshu.common.exception.BusinessException;
import com.hongshu.modules.content.entity.AiContent;
import com.hongshu.modules.content.mapper.AiContentMapper;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentService extends ServiceImpl<AiContentMapper, AiContent> {

    private final MinioClient minioClient;

    @Value("${minio.img-bucket}")
    private String imgBucket;

    @Value("${minio.endpoint}")
    private String minioEndpoint;

    // ===== 内容CRUD =====

    @Transactional
    public AiContent createContent(AiContent entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setAuditStatus(0);
        save(entity);
        return entity;
    }

    public AiContent getContentDetail(Long contentId) {
        AiContent content = getById(contentId);
        if (content == null) throw new BusinessException(404, "内容不存在");
        return content;
    }

    @Transactional
    public AiContent updateContent(Long contentId, AiContent update) {
        AiContent content = getContentDetail(contentId);
        if (content.getAuditStatus() == 2) {
            throw new BusinessException(400, "已审核通过的内容不可修改，请先撤销审核");
        }
        if (StrUtil.isNotBlank(update.getTitle())) content.setTitle(update.getTitle());
        if (StrUtil.isNotBlank(update.getContent())) content.setContent(update.getContent());
        if (StrUtil.isNotBlank(update.getImageUrls())) content.setImageUrls(update.getImageUrls());
        if (StrUtil.isNotBlank(update.getTags())) content.setTags(update.getTags());
        updateById(content);
        return content;
    }

    @Transactional
    public void deleteContent(Long contentId) {
        AiContent content = getContentDetail(contentId);
        content.setAuditStatus(4); // 已删除
        updateById(content);
    }

    // ===== 审核 =====

    @Transactional
    public void submitForAudit(Long contentId) {
        AiContent content = getContentDetail(contentId);
        if (content.getAuditStatus() != 0) {
            throw new BusinessException(400, "仅草稿状态可提交审核");
        }
        content.setAuditStatus(1);
        updateById(content);
    }

    @Transactional
    public void approveContent(Long contentId) {
        AiContent content = getContentDetail(contentId);
        if (content.getAuditStatus() != 1) {
            throw new BusinessException(400, "仅审核中状态可通过");
        }
        content.setAuditStatus(2);
        updateById(content);
    }

    @Transactional
    public void rejectContent(Long contentId, String reason) {
        AiContent content = getContentDetail(contentId);
        if (content.getAuditStatus() != 1) {
            throw new BusinessException(400, "仅审核中状态可拒绝");
        }
        content.setAuditStatus(3);
        updateById(content);
    }

    @Transactional
    public void batchApprove(List<Long> contentIds) {
        for (Long id : contentIds) {
            approveContent(id);
        }
    }

    @Transactional
    public void batchDelete(List<Long> contentIds) {
        for (Long id : contentIds) {
            deleteContent(id);
        }
    }

    // ===== 查询 =====

    public IPage<AiContent> pageUserContents(Long userId, int page, int pageSize,
                                              Integer auditStatus, String keyword) {
        LambdaQueryWrapper<AiContent> wrapper = new LambdaQueryWrapper<AiContent>()
                .eq(AiContent::getUserId, userId);
        if (auditStatus != null) wrapper.eq(AiContent::getAuditStatus, auditStatus);
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(AiContent::getTitle, keyword).or().like(AiContent::getContent, keyword);
        }
        wrapper.orderByDesc(AiContent::getCreateTime);
        return page(new Page<>(page, pageSize), wrapper);
    }

    public IPage<AiContent> pageAllContents(int page, int pageSize, Integer auditStatus,
                                             String keyword, String terminal) {
        LambdaQueryWrapper<AiContent> wrapper = new LambdaQueryWrapper<>();
        if (auditStatus != null) wrapper.eq(AiContent::getAuditStatus, auditStatus);
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(AiContent::getTitle, keyword);
        }
        if (StrUtil.isNotBlank(terminal)) {
            wrapper.eq(AiContent::getCreateTerminal, terminal);
        }
        wrapper.orderByDesc(AiContent::getCreateTime);
        return page(new Page<>(page, pageSize), wrapper);
    }

    // ===== 图片上传 =====

    public String uploadImage(MultipartFile file) {
        try {
            String objectName = "img/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(imgBucket)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            return minioEndpoint + "/" + imgBucket + "/" + objectName;
        } catch (Exception e) {
            log.error("图片上传失败", e);
            throw new BusinessException(500, "图片上传失败");
        }
    }

    public String getImagePresignedUrl(String objectPath) {
        try {
            String objectName = objectPath.replace(minioEndpoint + "/" + imgBucket + "/", "");
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(imgBucket)
                    .object(objectName)
                    .method(Method.GET)
                    .expiry(1, TimeUnit.HOURS)
                    .build());
        } catch (Exception e) {
            log.error("获取图片URL失败", e);
            return objectPath;
        }
    }
}
