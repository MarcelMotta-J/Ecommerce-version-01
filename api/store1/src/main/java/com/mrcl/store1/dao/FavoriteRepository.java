package com.mrcl.store1.dao;

import com.mrcl.store1.entity.Favorite;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserEmail(String userEmail);

    boolean existsByUserEmailAndProductId(String userEmail, Long productId);

    @Transactional
    long deleteByUserEmailAndProductId(String userEmail, Long productId);
}
