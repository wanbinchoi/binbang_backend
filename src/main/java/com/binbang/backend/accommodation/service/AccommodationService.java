package com.binbang.backend.accommodation.service;

import com.binbang.backend.accommodation.entity.AccommodationPolicy;
import com.binbang.backend.accommodation.exception.CategoryNotFoundException;
import com.binbang.backend.accommodation.dto.AccommodationRegisterDto;
import com.binbang.backend.accommodation.dto.AccommodationResponse;
import com.binbang.backend.accommodation.entity.Accommodation;
import com.binbang.backend.accommodation.repository.AccommodationPolicyRepository;
import com.binbang.backend.accommodation.repository.AccommodationRepository;
import com.binbang.backend.category.entity.Category;
import com.binbang.backend.category.repository.CategoryRepository;
import com.binbang.backend.global.exception.CustomException;
import com.binbang.backend.member.entity.Member;
import com.binbang.backend.member.exception.MemberNotFoundException;
import com.binbang.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;
    private final AccommodationPolicyRepository policyRepository;
    private final ObjectMapper objectMapper;

    public Member getCurrentMember(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException(email));
    }

    @Transactional
    public AccommodationResponse register(AccommodationRegisterDto dto) {
        Member member = getCurrentMember();

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(dto.getCategoryId()));

        Accommodation accommodation = Accommodation.builder()
                .member(member)
                .name(dto.getName())
                .address(dto.getAddress())
                .price(dto.getPrice())
                .checkInTime(dto.getCheckInTime())
                .checkOutTime(dto.getCheckOutTime())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .description(dto.getDescription())
                .category(category)
                .build();

        accommodationRepository.save(accommodation);

        String policyJson;
        try {
            policyJson = objectMapper.writeValueAsString(dto.getPolicy());
        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "정책 정보 처리 중 오류가 발생했습니다.");
        }
        AccommodationPolicy policy = new AccommodationPolicy();
        policy.setAccommodation(accommodation);
        policy.setPolicies(policyJson
        );

        policyRepository.save(policy);

        return AccommodationResponse.from(accommodation);
    }
}
