package com.example.blockchainmonitorservice.service;

import org.springframework.stereotype.Service;

@Service
public interface BtcService {
    boolean scanningProcess(Long fromBlock, Long toBlock);
}
