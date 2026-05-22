package com.hongshu.modules.member.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hongshu.common.model.ApiResponse;
import com.hongshu.common.model.PageResult;
import com.hongshu.modules.member.entity.MemberPlan;
import com.hongshu.modules.member.entity.MemberOrder;
import com.hongshu.modules.member.mapper.MemberPlanMapper;
import com.hongshu.modules.member.mapper.MemberOrderMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/membership")
@PreAuthorize("hasRole('ADMIN')")
public class MemberController {

    private final MemberPlanMapper planMapper;
    private final MemberOrderMapper orderMapper;

    public MemberController(MemberPlanMapper planMapper, MemberOrderMapper orderMapper) {
        this.planMapper = planMapper;
        this.orderMapper = orderMapper;
    }

    // ===== 会员套餐 =====
    @GetMapping("/plans")
    public ApiResponse<List<MemberPlan>> getPlans() {
        LambdaQueryWrapper<MemberPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberPlan::getDeleted, 0).orderByAsc(MemberPlan::getSortOrder);
        return ApiResponse.ok(planMapper.selectList(wrapper));
    }

    @PostMapping("/plans")
    public ApiResponse<MemberPlan> createPlan(@RequestBody MemberPlan plan) {
        plan.setCreateTime(LocalDateTime.now());
        plan.setUpdateTime(LocalDateTime.now());
        plan.setStatus(plan.getStatus() != null ? plan.getStatus() : 1);
        planMapper.insert(plan);
        return ApiResponse.ok(plan);
    }

    @PutMapping("/plans/{id}")
    public ApiResponse<?> updatePlan(@PathVariable Long id, @RequestBody MemberPlan plan) {
        plan.setId(id);
        plan.setCreateTime(null);
        plan.setUpdateTime(LocalDateTime.now());
        planMapper.updateById(plan);
        return ApiResponse.ok();
    }

    @DeleteMapping("/plans/{id}")
    public ApiResponse<?> deletePlan(@PathVariable Long id) {
        planMapper.deleteById(id);
        return ApiResponse.ok();
    }

    // ===== 会员订单 =====
    @GetMapping("/orders")
    public ApiResponse<PageResult<?>> getOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<MemberOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberOrder::getDeleted, 0);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(MemberOrder::getOrderNo, keyword)
                    .or().like(MemberOrder::getUserName, keyword)
                    .or().like(MemberOrder::getPlanName, keyword));
        }
        if (status != null) wrapper.eq(MemberOrder::getStatus, status);
        wrapper.orderByDesc(MemberOrder::getCreateTime);
        IPage<MemberOrder> result = orderMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return ApiResponse.ok(PageResult.of(result.getTotal(), page, pageSize, result.getRecords()));
    }

    @PostMapping("/orders")
    public ApiResponse<MemberOrder> createOrder(@RequestBody MemberOrder order) {
        if (order.getOrderNo() == null || order.getOrderNo().isEmpty()) {
            order.setOrderNo("ORD" + System.currentTimeMillis());
        }
        order.setCreateTime(LocalDateTime.now());
        orderMapper.insert(order);
        return ApiResponse.ok(order);
    }

    @PutMapping("/orders/{id}")
    public ApiResponse<?> updateOrder(@PathVariable Long id, @RequestBody MemberOrder order) {
        order.setId(id);
        orderMapper.updateById(order);
        return ApiResponse.ok();
    }

    @DeleteMapping("/orders/{id}")
    public ApiResponse<?> deleteOrder(@PathVariable Long id) {
        orderMapper.deleteById(id);
        return ApiResponse.ok();
    }
}
