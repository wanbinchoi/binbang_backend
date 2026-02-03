package com.binbang.backend.accommodation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccommodationListResponse {

    private Long accommodationId;

    private String name;

    private Long price;
}
