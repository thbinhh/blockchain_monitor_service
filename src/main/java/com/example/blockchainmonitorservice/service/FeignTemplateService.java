package com.example.blockchainmonitorservice.service;


import com.example.blockchainmonitorservice.dto.request.BtcRequestDto;
import com.example.blockchainmonitorservice.dto.response.BtcRawResponseDto;
import com.example.blockchainmonitorservice.dto.response.LatestBlockResponseDto;
import com.example.blockchainmonitorservice.dto.response.TransactionResponseDto;
import com.example.blockchainmonitorservice.feign.FeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeignTemplateService<T> {
    private final FeignClient feignClient;

    public BtcRawResponseDto<LatestBlockResponseDto> getLatestBlock(String chain){
        return feignClient.getLatestBlock(chain);
    }

    public BtcRawResponseDto<TransactionResponseDto> getTransactionByBlockNumber(String chain, BtcRequestDto btcRequestDto) {
        return feignClient.getTransactionByBlockNumber(chain, btcRequestDto);
    }

}
