package com.example.blockchainmonitorservice.controller;

import com.example.blockchainmonitorservice.dto.request.BtcRequestDto;
import com.example.blockchainmonitorservice.service.BtcService;
import com.example.blockchainmonitorservice.service.Impl.BtcServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/btc/v1/scanning")
public class ScanningProcessController {

    @Autowired
    private BtcService btcService;

    @Autowired
    private BtcServiceImpl btcServiceImpl;

    @PostMapping("test")
    public Object scanningProcess() {
        return btcService.scanningProcess(2410475L,2410480L);
    }
}
