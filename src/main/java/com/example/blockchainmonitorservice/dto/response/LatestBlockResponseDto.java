package com.example.blockchainmonitorservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LatestBlockResponseDto {
    @JsonProperty(value = "blockNumber")
    private Long blockNumber;
}
