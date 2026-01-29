package com.binbang.backend.accommodation.service;

import com.binbang.backend.accommodation.dto.AccommodationRegisterDto;
import com.binbang.backend.accommodation.dto.AccommodationResponse;
import com.binbang.backend.accommodation.entity.Accommodation;
import com.binbang.backend.accommodation.repository.AccommodationRepository;
import com.binbang.backend.category.entity.Category;
import com.binbang.backend.category.repository.CategoryRepository;
import com.binbang.backend.member.entity.Member;
import com.binbang.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;

    public Member getCurrentMember(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
    }

    @Transactional
    public AccommodationResponse register(AccommodationRegisterDto dto){
        Member member = getCurrentMember();

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("카테고리가 존재하지 않습니다."));

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

        return AccommodationResponse.from(accommodation);
    }
}
