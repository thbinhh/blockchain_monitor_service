package com.example.blockchainmonitorservice.controller;

import com.example.blockchainmonitorservice.dto.request.BtcRequestDto;
import com.example.blockchainmonitorservice.service.BtcService;
import com.example.blockchainmonitorservice.service.FeignTemplateService;
import com.example.blockchainmonitorservice.service.Impl.BtcServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/btc/v1/scanning")
public class ScanningProcessController {

    @Autowired
    private BtcService btcService;

    @GetMapping("rest")
    public boolean scanningProcessRest(@RequestParam Long fromBlock, @RequestParam Long toBlock) {
        if(fromBlock > toBlock) {
            return false;
        }
        return btcService.scanningProcessRest(fromBlock,toBlock);
    }

    @GetMapping("feign")
    public boolean scanningProcessFeign(@RequestParam Long fromBlock, @RequestParam Long toBlock) {
        if(fromBlock > toBlock) {
            return false;
        }
        return btcService.scanningProcessFeign(fromBlock,toBlock);
    }
}
