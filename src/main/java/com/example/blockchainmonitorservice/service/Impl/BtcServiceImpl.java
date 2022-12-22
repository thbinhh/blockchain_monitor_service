package com.example.blockchainmonitorservice.service.Impl;

import com.example.blockchainmonitorservice.constant.ScanningProcessChainEnum;
import com.example.blockchainmonitorservice.constant.ScanningProcessStatusEnum;
import com.example.blockchainmonitorservice.constant.ScanningProcessTopicEnum;
import com.example.blockchainmonitorservice.dto.kafkamessage.Transaction;
import com.example.blockchainmonitorservice.dto.request.BtcRequestDto;
import com.example.blockchainmonitorservice.dto.response.BtcRawResponseDto;
import com.example.blockchainmonitorservice.dto.response.LatestBlockResponseDto;
import com.example.blockchainmonitorservice.dto.response.TransactionResponseDto;
import com.example.blockchainmonitorservice.entity.ScanningProcess;
import com.example.blockchainmonitorservice.repository.AddressDao;
import com.example.blockchainmonitorservice.repository.ScanningProcessRepository;
import com.example.blockchainmonitorservice.service.BtcService;
import com.example.blockchainmonitorservice.service.FeignTemplateService;
import com.example.blockchainmonitorservice.service.RestTemplateService;
import com.example.blockchainmonitorservice.service.producer.Producer;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BtcServiceImpl implements BtcService {

    @Autowired
    private ScanningProcessRepository scanningProcessRepository;

    @Autowired
    private RestTemplateService<LatestBlockResponseDto> latestblockdto;

    @Autowired
    private RestTemplateService<TransactionResponseDto> transactionResponseDto;

    @Autowired
    private FeignTemplateService<LatestBlockResponseDto> latestBlockResponseDtoFeign;

    @Autowired
    private FeignTemplateService<TransactionResponseDto> transactionResponseDtoFeign;

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private Producer producer;

    private String chain = "btc";

    @Override
    public boolean scanningProcessRest(Long fromBlock, Long toBlock) {
        if(!isScanningProcess()) {
            Long latestBlockHeight = getLatestBlockHeightRest();
            if(latestBlockHeight < toBlock)
            {
                toBlock = latestBlockHeight;
            }
            Long processInsertId = insertScanProcess(fromBlock, toBlock);

            List<Long> listBlockNumber = new ArrayList<>();
            for(long i = fromBlock; i <= toBlock; i++)
            {
                listBlockNumber.add(i);
            }

            getTransactionByBlockNumber(listBlockNumber);
            updateProcessEvent(processInsertId);
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean scanningProcessFeign(Long fromBlock, Long toBlock) {
        if(!isScanningProcess()) {
            Long latestBlockHeight = getLatestBlockHeightFeign();
            if(latestBlockHeight < toBlock)
            {
                toBlock = latestBlockHeight;
            }
            Long processInsertId = insertScanProcess(fromBlock, toBlock);

            List<Long> listBlockNumber = new ArrayList<>();
            for(long i = fromBlock; i <= toBlock; i++)
            {
                listBlockNumber.add(i);
            }

            getTransactionByBlockNumberFeign(listBlockNumber);
            updateProcessEvent(processInsertId);
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isScanningProcess() {
        boolean isScanning = scanningProcessRepository.existsByStatus(ScanningProcessStatusEnum.IN_PROGRESS);
        System.out.println(ScanningProcessStatusEnum.IN_PROGRESS.toString().getClass());
        return false;
    }

    private long getLatestBlockHeightRest(){
        String url = "latest";
        BtcRawResponseDto<LatestBlockResponseDto> latestBlockResponseDto = latestblockdto.callExchange(new ParameterizedTypeReference<>() {},null, url);
        return latestBlockResponseDto.getResult().getBlockNumber();
    }

    private long getLatestBlockHeightFeign(){
        BtcRawResponseDto<LatestBlockResponseDto> latestBlockResponseDto = latestBlockResponseDtoFeign.getLatestBlock(chain);
        return latestBlockResponseDto.getResult().getBlockNumber();
    }

    private void getTransactionByBlockNumber(List<Long> listBlockNumber){
        String url = "transactions";
        listBlockNumber.forEach((blockNumber) -> {
            BtcRequestDto btcRequestDto = new BtcRequestDto();
            btcRequestDto.setHashOrNumber(String.valueOf(blockNumber));
            BtcRawResponseDto<TransactionResponseDto> transactionBtcRawResponseDto = transactionResponseDto.callExchange(new ParameterizedTypeReference<>() {},btcRequestDto, url);
            transactionBtcRawResponseDto.getResult().getTransactions().forEach((element) -> {
                Map<String,List<String>> transactionResponseDtoList = new HashMap<>();
                List<String> listAddress = new ArrayList<>();
                if (element.getFrom() != null) {
                    listAddress.addAll(element.getFrom());
                }
                if (element.getTo() != null) {
                    listAddress.addAll(element.getTo());
                }
                transactionResponseDtoList.put(element.getTxid(), listAddress);
                List<Transaction> transactionList = getTransactionHaveAddress(transactionResponseDtoList);
                sendMessageKafka(transactionList);
            });
        });
    }

    private void getTransactionByBlockNumberFeign(List<Long> listBlockNumber){
        listBlockNumber.forEach((blockNumber) -> {
            BtcRequestDto btcRequestDto = new BtcRequestDto();
            btcRequestDto.setHashOrNumber(String.valueOf(blockNumber));
            BtcRawResponseDto<TransactionResponseDto> transactionBtcRawResponseDto = transactionResponseDtoFeign.getTransactionByBlockNumber(chain,btcRequestDto);
            transactionBtcRawResponseDto.getResult().getTransactions().forEach((element) -> {
                Map<String,List<String>> transactionResponseDtoList = new HashMap<>();
                List<String> listAddress = new ArrayList<>();
                if (element.getFrom() != null) {
                    listAddress.addAll(element.getFrom());
                }
                if (element.getTo() != null) {
                    listAddress.addAll(element.getTo());
                }
                transactionResponseDtoList.put(element.getTxid(), listAddress);
                List<Transaction> transactionList = getTransactionHaveAddress(transactionResponseDtoList);
                sendMessageKafka(transactionList);
            });
        });
    }

    private long insertScanProcess(Long fromBlock, Long toBlock) {
        ScanningProcess scanningProcess = new ScanningProcess();
        Timestamp timeStart = new Timestamp(System.currentTimeMillis());
        scanningProcess.setStartAt(timeStart);
        scanningProcess.setFromBlock(fromBlock);
        scanningProcess.setChain(ScanningProcessChainEnum.BTC);
        scanningProcess.setStatus(ScanningProcessStatusEnum.IN_PROGRESS);
        scanningProcess.setToBlock(toBlock);
        scanningProcessRepository.save(scanningProcess);
        //exception
        return scanningProcess.getId();
    }

    private String checkAddressValue(String addressString) {
        String value = addressDao.findValueByKey(addressString);
        if (value != null)
        {
            return value;
        }
        return null;
    }

    private boolean updateProcessEvent(Long processId) {
        ScanningProcess scanningProcess = scanningProcessRepository.getReferenceById(processId);
        scanningProcess.setStatus(ScanningProcessStatusEnum.COMPLETE);
        Timestamp timeEnd = new Timestamp(System.currentTimeMillis());
        scanningProcess.setEndAt(timeEnd);
        scanningProcessRepository.save(scanningProcess);
        return true;
    }

    private List<Transaction> getTransactionHaveAddress(Map<String,List<String>> listTransaction) {
        List<Transaction> transactionList = new ArrayList<>();
        for (var entry : listTransaction.entrySet()) {
            entry.getValue().forEach((element) -> {
                String valueAddress = checkAddressValue(chain + "_" + element);
                if(valueAddress != null)
                {
                    Transaction transaction = new Transaction(entry.getKey().toLowerCase(), valueAddress);
                    transactionList.add(transaction);
                }
            });
        }
        return transactionList;
    }

    private void sendMessageKafka(List<Transaction> transactionList) {
        transactionList.forEach((element) -> {
            try {
                if(element.getValue().equals(String.valueOf(ScanningProcessTopicEnum.DEPOSIT)))
                {
                    producer.sendMessage(element,String.valueOf(ScanningProcessTopicEnum.DEPOSIT));
                } else {
                    producer.sendMessage(element,String.valueOf(ScanningProcessTopicEnum.WITHDRAW));
                }
            } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
            }
        });
    }
}
