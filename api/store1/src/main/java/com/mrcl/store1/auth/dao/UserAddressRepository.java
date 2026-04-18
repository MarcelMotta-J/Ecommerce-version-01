package com.mrcl.store1.auth.dao;

import com.mrcl.store1.auth.dto.UserAddressResponse;
import com.mrcl.store1.auth.entity.UserAddress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;




import java.util.List;
import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    List<UserAddress> findByUserEmailOrderByIsDefaultDesc(String email);

    Optional<UserAddress> findByIdAndUserEmail(Long id, String email);

    @Modifying
    @Query("UPDATE UserAddress a SET a.isDefault = false WHERE a.user.email = :email")
    void resetDefaultForUser(@Param("email") String email);



    boolean existsByUserEmailAndIsDefaultTrue(String email);

    Optional<UserAddress> findByUserEmailAndStreetAndCityAndStateAndCountryAndZipCode(
            String email,
            String street,
            String city,
            String state,
            String country,
            String zipCode);


    Page<UserAddress> findByUserEmailOrderByIsDefaultDesc(String email, Pageable pageable);

    Page<UserAddress> findByUserEmailOrderByIsDefaultDescCreatedAtDesc(String email, Pageable pageable);

    Page<UserAddress> findByUserEmailAndCityContainingIgnoreCase(
            String email, String city, Pageable pageable);

    Page<UserAddress> findByUserEmailAndStateContainingIgnoreCase(
            String email, String state, Pageable pageable);
}
