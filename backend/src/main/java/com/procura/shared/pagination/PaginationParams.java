package com.procura.shared.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record PaginationParams(
    Integer page,
    Integer size,
    String sortBy,
    String sortDir
) {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    public Pageable toPageable() {
        int pageNumber = (page != null && page >= 0) ? page : DEFAULT_PAGE;
        int pageSize = (size != null && size > 0) ? Math.min(size, MAX_SIZE) : DEFAULT_SIZE;
        Sort.Direction direction = (sortDir != null && sortDir.equalsIgnoreCase("asc")) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String property = (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt";

        return PageRequest.of(pageNumber, pageSize, Sort.by(direction, property));
    }
}
