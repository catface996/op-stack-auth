package com.catface996.auth.common.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 分页请求基类
 * 所有需要分页的请求 DTO 应继承此类
 */
@Data
@Schema(description = "分页请求基类")
public class PageableRequest {

    @Schema(description = "页码（从1开始）", example = "1", minimum = "1")
    @Min(value = 1, message = "页码最小为1")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "20", minimum = "1", maximum = "100")
    @Min(value = 1, message = "每页大小最小为1")
    @Max(value = 100, message = "每页大小最大为100")
    private Integer size = 20;

    @Schema(description = "租户ID（网关注入）", hidden = true)
    private Long tenantId;

    @Schema(description = "追踪ID（网关注入）", hidden = true)
    private String traceId;

    @Schema(description = "用户ID（网关注入）", hidden = true)
    private Long userId;

    /**
     * 获取偏移量（用于数据库查询）
     */
    public int getOffset() {
        return (page - 1) * size;
    }
}
