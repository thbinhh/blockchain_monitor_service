package com.example.blockchainmonitorservice.service;

import com.example.blockchainmonitorservice.dto.request.BtcRequestDto;
import com.example.blockchainmonitorservice.dto.response.BtcRawResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class RestTemplateService<T> {

    private String api = "http://localhost:5001/btc/v1/block/";

    public BtcRawResponseDto<T> callExchange(ParameterizedTypeReference<BtcRawResponseDto<T>> responseType, BtcRequestDto btcRequestDto, String url) {
        log.info("Start call method exchange");
        String urlFull = api + url;
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        if(btcRequestDto == null)
        {
            BtcRawResponseDto<T> exchange = restTemplate.exchange(urlFull, HttpMethod.POST, null, responseType).getBody();
//            log.info("Response call method post {}", exchange);
            return exchange;
        }
        else
        {
            Map<String, Object> body = this.createBody(btcRequestDto);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            BtcRawResponseDto<T> exchange = restTemplate.exchange(urlFull, HttpMethod.POST, entity, responseType).getBody();
//            log.info("Response call method post {}", exchange);
            return exchange;
        }
    }

    private Map<String, Object> createBody(BtcRequestDto btcRequestDto) {
        Map<String, Object> body = new HashMap<>();
        body.put("hashOrNumber", btcRequestDto.getHashOrNumber());
        return body;
    }
}
