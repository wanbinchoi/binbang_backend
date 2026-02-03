package com.binbang.backend.accommodation.service;

import com.binbang.backend.accommodation.dto.AccommodationFacilityDto;
import com.binbang.backend.accommodation.dto.AccommodationListResponse;
import com.binbang.backend.accommodation.entity.AccommodationFacility;
import com.binbang.backend.accommodation.entity.AccommodationPolicy;
import com.binbang.backend.accommodation.exception.CategoryNotFoundException;
import com.binbang.backend.accommodation.dto.AccommodationRegisterDto;
import com.binbang.backend.accommodation.dto.AccommodationResponse;
import com.binbang.backend.accommodation.entity.Accommodation;
import com.binbang.backend.accommodation.repository.AccommodationFacilityRepository;
import com.binbang.backend.accommodation.repository.AccommodationPolicyRepository;
import com.binbang.backend.accommodation.repository.AccommodationRepository;
import com.binbang.backend.accommodation.specification.AccommodationSpecification;
import com.binbang.backend.category.entity.Category;
import com.binbang.backend.category.repository.CategoryRepository;
import com.binbang.backend.global.exception.CustomException;
import com.binbang.backend.member.entity.Member;
import com.binbang.backend.member.exception.MemberNotFoundException;
import com.binbang.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;
    private final AccommodationFacilityRepository facilityRepository;
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

        AccommodationFacility facility = AccommodationFacility.builder()
                .accommodation(accommodation)
                .bedrooms(dto.getFacility().getBedrooms())
                .bathrooms(dto.getFacility().getBathrooms())
                .beds(dto.getFacility().getBeds())
                .petAllowed(dto.getFacility().isPetAllowed())
                .parkingAvailable(dto.getFacility().isParkingAvailable())
                .hasBbq(dto.getFacility().isHasBbq())
                .hasWifi(dto.getFacility().isHasWifi())
                .build();

        facilityRepository.save(facility);

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

    //전체 조회
//    public Page<AccommodationListResponse> getList(Pageable pageable){
//        Page<Accommodation> accommodationPage = accommodationRepository.findAll(pageable);
//
//        //Page<Accommodation>를 Page<AccommodationListResponse>로 변환
//
//        return accommodationPage.map(
//                accommodation ->
//                new AccommodationListResponse(
//                        accommodation.getAccommodationId(),
//                        accommodation.getName(),
//                        accommodation.getPrice()
//                )
//        );
//    }

    //카테고리별 조회 없으면 전체조회
//   @Transactional
//    public Page<AccommodationListResponse> getList(Long categoryId, Pageable pageable){
//        Page<Accommodation> accommodationPage;
//
//        if(categoryId == null){
//            accommodationPage = accommodationRepository.findAll(pageable);
//        }else{
//            accommodationPage = accommodationRepository.findByCategory_CategoryId(categoryId, pageable);
//        }
//        //Page<Accommodation>를 Page<AccommodationListResponse>로 변환
//
//        return accommodationPage.map(
//                accommodation ->
//                        new AccommodationListResponse(
//                                accommodation.getAccommodationId(),
//                                accommodation.getName(),
//                                accommodation.getPrice()
//                        )
//        );
//    }

    @Transactional
    public Page<AccommodationListResponse> getList(
            Long categoryId,
            Integer minBedrooms,
            Integer minBathrooms,
            Integer minBeds,
            Boolean petAllowed,
            Boolean parkingAvailable,
            Boolean hasBbq,
            Boolean hasWifi,
            Pageable pageable
    ){
        //Specification 조합
        Specification<Accommodation> spec = Specification
                .where(AccommodationSpecification.hasCategory(categoryId))
                .and(AccommodationSpecification.hasMinBedrooms(minBedrooms))
                .and(AccommodationSpecification.hasMinBathrooms(minBathrooms))
                .and(AccommodationSpecification.hasMinBeds(minBeds))
                .and(AccommodationSpecification.petAllowed(petAllowed))
                .and(AccommodationSpecification.parkingAvailable(parkingAvailable))
                .and(AccommodationSpecification.hasBbq(hasBbq))
                .and(AccommodationSpecification.hasWifi(hasWifi));

        //위에서 만든 조건으로 페이징처리하여 조회
        Page<Accommodation> accommodationPage = accommodationRepository.findAll(spec, pageable);

        //DTO 변환
        return accommodationPage.map(
                accommodation ->
                        new AccommodationListResponse(
                                accommodation.getAccommodationId(),
                                accommodation.getName(),
                                accommodation.getPrice()
                        )
        );
    }
}
