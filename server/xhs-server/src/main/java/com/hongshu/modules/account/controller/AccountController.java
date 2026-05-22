package com.hongshu.modules.account.controller;

import com.hongshu.common.model.ApiResponse;
import com.hongshu.modules.account.service.AccountService;
import com.hongshu.modules.account.entity.XiaohongshuAccount;
import com.hongshu.modules.account.entity.XhAccountData;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/list")
    public ApiResponse<List<XiaohongshuAccount>> getAccounts(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.ok(accountService.getUserAccounts(userId));
    }

    @PostMapping("/bind")
    public ApiResponse<XiaohongshuAccount> bindAccount(Authentication auth,
                                                        @RequestBody Map<String, String> body) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.ok(accountService.bindAccount(userId,
                body.get("account"), body.get("password")));
    }

    @DeleteMapping("/unbind/{accountId}")
    public ApiResponse<?> unbindAccount(Authentication auth, @PathVariable Long accountId) {
        Long userId = (Long) auth.getPrincipal();
        accountService.unbindAccount(userId, accountId);
        return ApiResponse.ok("解绑成功");
    }

    @GetMapping("/data/{accountId}")
    public ApiResponse<XhAccountData> getDayData(@PathVariable Long accountId,
                                                  @RequestParam(required = false)
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.ok(accountService.getAccountDayData(accountId, date));
    }

    @GetMapping("/data/{accountId}/history")
    public ApiResponse<List<XhAccountData>> getHistoryData(
            @PathVariable Long accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ApiResponse.ok(accountService.getAccountHistoryData(accountId, startDate, endDate));
    }
}
