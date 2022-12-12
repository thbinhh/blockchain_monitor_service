package com.example.blockchainmonitorservice.dto.response;

import com.example.blockchainmonitorservice.dto.response.raw.RawTransactionDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TransactionResponseDto {
    @JsonProperty(value = "transactions")
    private List<RawTransactionDto> transactions;
}
