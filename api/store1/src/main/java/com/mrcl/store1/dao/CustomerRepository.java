package com.mrcl.store1.dao;

import com.mrcl.store1.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mrcl.store1.admin.dto.AdminCustomerRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("""
    SELECT new com.mrcl.store1.admin.dto.AdminCustomerRow(
    c.id,
    c.firstName,
    c.lastName,
    c.email,
    COUNT(o.id)
    )
        FROM Customer c
        LEFT JOIN Order o ON o.customer.id = c.id
        GROUP BY c.id, c.firstName, c.lastName, c.email
        ORDER BY c.id DESC
    """)
    Page<AdminCustomerRow> findAdminCustomers(Pageable pageable);


}
