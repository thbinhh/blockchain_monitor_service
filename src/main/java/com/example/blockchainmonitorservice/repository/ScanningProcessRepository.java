package com.example.blockchainmonitorservice.repository;

import com.example.blockchainmonitorservice.constant.ScanningProcessStatusEnum;
import com.example.blockchainmonitorservice.entity.ScanningProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScanningProcessRepository extends JpaRepository<ScanningProcess, Long> {
    boolean existsByStatus(ScanningProcessStatusEnum process);
}
