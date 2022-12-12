package com.example.blockchainmonitorservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "scanning_process")
public class ScanningProcess {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "start_at")
    private Timestamp startAt;

    @Column(name = "end_at")
    private Timestamp endAt;

    @Column(name = "chain")
    private String chain;

    @Column(name = "from_block")
    private Long fromBlock;

    @Column(name = "to_block")
    private Long toBlock;

    @Column(name = "status")
    private String status;
}
