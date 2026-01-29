package com.binbang.backend.accommodation.controller;

import com.binbang.backend.accommodation.dto.AccommodationRegisterDto;
import com.binbang.backend.accommodation.dto.AccommodationResponse;
import com.binbang.backend.accommodation.service.AccommodationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accommodation")
@RequiredArgsConstructor
public class AccommodationController {

    private final AccommodationService accommodationService;

    @PostMapping("/register")
    public ResponseEntity<AccommodationResponse> register(@Valid @RequestBody AccommodationRegisterDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(accommodationService.register(dto));
    }
}
