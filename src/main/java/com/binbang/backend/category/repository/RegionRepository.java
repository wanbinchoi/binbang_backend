package com.binbang.backend.category.repository;

import com.binbang.backend.category.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findByDepth(int depth);

    List<Region> findByParent(Region parent);

    Region findByName(String name);


}
