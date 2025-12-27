package com.catface996.auth.common.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 分页响应结果
 * 用于包装分页查询的返回数据
 */
@Data
@Schema(description = "分页响应结果")
public class PageResult<T> {

    @Schema(description = "响应码，0表示成功", example = "0")
    private Integer code;

    @Schema(description = "响应消息", example = "success")
    private String message;

    @Schema(description = "是否成功", example = "true")
    private Boolean success;

    @Schema(description = "分页数据")
    private PageData<T> data;

    /**
     * 分页数据内部类
     */
    @Data
    @Schema(description = "分页数据")
    public static class PageData<T> {

        @Schema(description = "数据列表")
        private List<T> content;

        @Schema(description = "当前页码（从1开始）", example = "1")
        private Integer page;

        @Schema(description = "每页大小", example = "20")
        private Integer size;

        @Schema(description = "总记录数", example = "100")
        private Long totalElements;

        @Schema(description = "总页数", example = "5")
        private Integer totalPages;

        @Schema(description = "是否为第一页", example = "true")
        private Boolean first;

        @Schema(description = "是否为最后一页", example = "false")
        private Boolean last;
    }

    /**
     * 创建成功的分页响应
     */
    public static <T> PageResult<T> success(List<T> content, int page, int size, long totalElements) {
        PageResult<T> result = new PageResult<>();
        result.setCode(0);
        result.setMessage("success");
        result.setSuccess(true);

        PageData<T> pageData = new PageData<>();
        pageData.setContent(content);
        pageData.setPage(page);
        pageData.setSize(size);
        pageData.setTotalElements(totalElements);
        pageData.setTotalPages((int) Math.ceil((double) totalElements / size));
        pageData.setFirst(page == 1);
        pageData.setLast(page >= pageData.getTotalPages());

        result.setData(pageData);
        return result;
    }

    /**
     * 创建空的分页响应
     */
    public static <T> PageResult<T> empty(int page, int size) {
        return success(Collections.emptyList(), page, size, 0);
    }

    /**
     * 创建失败的分页响应
     */
    public static <T> PageResult<T> failure(int code, String message) {
        PageResult<T> result = new PageResult<>();
        result.setCode(code);
        result.setMessage(message);
        result.setSuccess(false);
        return result;
    }
}
