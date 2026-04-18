package com.mrcl.store1.admin.dto;
import java.util.List;
/**
 * Generic paged response for admin screens.
 */
public record AdminPagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
}
