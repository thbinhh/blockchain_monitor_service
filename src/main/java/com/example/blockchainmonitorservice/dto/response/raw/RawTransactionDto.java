package com.example.blockchainmonitorservice.dto.response.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RawTransactionDto {
    @JsonProperty(value = "txid")
    private String txid;
    @JsonProperty(value = "from")
    private List<String> from;
    @JsonProperty(value = "to")
    private List<String> to;
}
