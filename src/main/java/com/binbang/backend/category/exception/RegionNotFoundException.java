package com.binbang.backend.category.exception;

import com.binbang.backend.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class RegionNotFoundException extends CustomException {
    public RegionNotFoundException(Long parentId) {
        super(HttpStatus.NOT_FOUND, "존재하지 않는 지역 입니다: " + parentId);
    }
}
