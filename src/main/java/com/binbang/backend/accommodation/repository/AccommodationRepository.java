package com.binbang.backend.accommodation.repository;

import com.binbang.backend.accommodation.entity.Accommodation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, Long>, JpaSpecificationExecutor<Accommodation> {

    Page<Accommodation> findAll(Pageable pageable);

    Page<Accommodation> findByCategory_CategoryId(Long categoryId, Pageable pageable);


}
