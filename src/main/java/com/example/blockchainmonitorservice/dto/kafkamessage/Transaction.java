package com.example.blockchainmonitorservice.dto.kafkamessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private String txId;
    private String value;
}
