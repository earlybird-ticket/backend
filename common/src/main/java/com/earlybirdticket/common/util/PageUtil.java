package com.earlybirdticket.common.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

public class PageUtil {

    public static Pageable getPageable(int page, int size, String sort, String orderBy) {

        Sort sortAndOrderBy = Sort.by(SortBy.getOrderBy(sort).getSortBy()).ascending();

        if (StringUtils.hasText(orderBy)) {
            if (orderBy.equalsIgnoreCase("ASC")) {
                sortAndOrderBy = sortAndOrderBy.ascending();
            } else {
                sortAndOrderBy = sortAndOrderBy.descending();
            }
        }

        return PageRequest.of(page - 1, getSize(size), sortAndOrderBy);
    }

    public static int getSize(int size) {
        return switch (size) {
            case 10 -> 10;
            case 30 -> 30;
            case 50 -> 50;
            default -> 10;
        };
    }

    @Getter
    @RequiredArgsConstructor
    public enum SortBy {
        CREATED_AT("createdAt"),
        MODIFIED_AT("modifiedAt");

        private final String sortBy;

        public static SortBy getOrderBy(String sortBy) {
            for (SortBy sort : SortBy.values()) {
                if (sort.getSortBy().equalsIgnoreCase(sortBy)) {
                    return sort;
                }
            }
            return CREATED_AT;
        }
    }

}

