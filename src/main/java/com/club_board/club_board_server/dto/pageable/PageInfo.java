package com.club_board.club_board_server.dto.pageable;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Getter
@Builder
public class PageInfo {
    private int page;
    private int size;
    private List<Order> sort;
    private int totalPages;
    private long totalElements;

    @Getter
    @Builder
    private static class Order {
        private Sort.Direction direction;
        private String property;
    }

    public static PageInfo from(Pageable pageable, Page<?> page) {
        return PageInfo.builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .sort(pageable.getSort().stream()
                        .map(sort -> Order.builder()
                                .direction(sort.getDirection())
                                .property(sort.getProperty())
                                .build())
                        .toList())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();
    }
}
