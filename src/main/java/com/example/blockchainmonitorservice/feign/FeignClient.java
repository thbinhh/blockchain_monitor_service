package com.example.blockchainmonitorservice.feign;

import com.example.blockchainmonitorservice.dto.request.BtcRequestDto;
import com.example.blockchainmonitorservice.dto.response.BtcRawResponseDto;
import com.example.blockchainmonitorservice.dto.response.LatestBlockResponseDto;
import com.example.blockchainmonitorservice.dto.response.TransactionResponseDto;
import feign.Body;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@org.springframework.cloud.openfeign.FeignClient(name="${full-node-service.name}", url = "${full-node-service.domain-url}")
public interface FeignClient {
    @PostMapping(path = "${full-node-service.get-latest-block.url}")
    BtcRawResponseDto<LatestBlockResponseDto> getLatestBlock(@PathVariable("chain") String chain);

    @PostMapping(path = "${full-node-service.get-transaction.url}")
    BtcRawResponseDto<TransactionResponseDto> getTransactionByBlockNumber(@PathVariable("chain") String chain, @RequestBody BtcRequestDto btcRequestDto);
}
