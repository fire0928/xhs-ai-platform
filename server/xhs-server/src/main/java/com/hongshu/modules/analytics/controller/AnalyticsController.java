package com.hongshu.modules.analytics.controller;

import com.hongshu.common.model.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> getDashboard(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.ok(Map.of(
                "todayPublish", 4,
                "totalInteractions", 2847,
                "fanGrowth", 156,
                "aiCreationCount", 12,
                "readCount", 128500L,
                "interactionRate", 5.7
        ));
    }

    @GetMapping("/content-rankings")
    public ApiResponse<?> getContentRankings(Authentication auth) {
        return ApiResponse.ok("排行榜功能开发中");
    }

    @GetMapping("/trends")
    public ApiResponse<?> getTrends(Authentication auth,
                                     @RequestParam(defaultValue = "7") int days) {
        return ApiResponse.ok("趋势数据功能开发中");
    }
}
