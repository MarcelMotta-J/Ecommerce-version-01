package com.mrcl.store1.admin.dao;

import com.mrcl.store1.admin.entity.AdminActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminActionLogRepository extends JpaRepository<AdminActionLog, Long> {

    List<AdminActionLog> findAllByOrderByTimestampDesc();

    Page<AdminActionLog> findAllByOrderByTimestampDesc(Pageable pageable);
}
