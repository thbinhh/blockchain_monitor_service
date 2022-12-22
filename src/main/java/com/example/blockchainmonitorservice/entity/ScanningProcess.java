package com.example.blockchainmonitorservice.entity;

import com.example.blockchainmonitorservice.constant.ScanningProcessChainEnum;
import com.example.blockchainmonitorservice.constant.ScanningProcessStatusEnum;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "scanning_process")
public class ScanningProcess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_at")
    private Timestamp startAt;

    @Column(name = "end_at")
    private Timestamp endAt;

    @Enumerated(EnumType.STRING)
    private ScanningProcessChainEnum chain;

    @Column(name = "from_block")
    private Long fromBlock;

    @Column(name = "to_block")
    private Long toBlock;

    @Enumerated(EnumType.STRING)
    private ScanningProcessStatusEnum status;
}
