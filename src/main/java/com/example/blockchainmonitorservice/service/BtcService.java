package com.example.blockchainmonitorservice.service;

import org.springframework.stereotype.Service;

@Service
public interface BtcService {
    boolean scanningProcessRest(Long fromBlock, Long toBlock);
    boolean scanningProcessFeign(Long fromBlock, Long toBlock);
}
