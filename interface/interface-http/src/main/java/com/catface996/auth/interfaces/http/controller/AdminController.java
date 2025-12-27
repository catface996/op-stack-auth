package com.catface996.auth.interfaces.http.controller;

import com.catface996.auth.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 管理员控制器 - 仅限管理员访问的管理功能
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/v1")
@RequiredArgsConstructor
@Tag(name = "管理员功能", description = "管理员专属接口，需要ADMIN角色权限才能访问")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @Operation(summary = "管理员仪表盘", description = "获取管理员仪表盘数据，包括当前用户信息和权限列表。权限由Gateway通过Auth服务统一校验。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功，返回仪表盘数据"),
            @ApiResponse(responseCode = "401", description = "未认证，请先登录")
    })
    @GetMapping("/dashboard")
    public ResponseEntity<Result<Map<String, Object>>> getDashboard(Authentication authentication) {
        log.info("Admin dashboard accessed by: {}", authentication.getName());

        Map<String, Object> dashboardData = Map.of(
                "message", "Welcome to the admin dashboard",
                "user", authentication.getName(),
                "authorities", authentication.getAuthorities().stream()
                        .map(Object::toString)
                        .toList()
        );

        return ResponseEntity.ok(Result.success(dashboardData));
    }

    @Operation(summary = "系统统计信息", description = "获取系统运行状态和版本等统计信息。权限由Gateway通过Auth服务统一校验。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功，返回系统统计信息"),
            @ApiResponse(responseCode = "401", description = "未认证，请先登录")
    })
    @GetMapping("/stats")
    public ResponseEntity<Result<Map<String, Object>>> getStats() {
        Map<String, Object> stats = Map.of(
                "status", "healthy",
                "version", "1.0.0"
        );

        return ResponseEntity.ok(Result.success(stats));
    }
}
