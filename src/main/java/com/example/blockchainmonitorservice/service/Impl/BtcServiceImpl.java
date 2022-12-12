package com.example.blockchainmonitorservice.service.Impl;

import com.example.blockchainmonitorservice.constant.ScanningProcessConstant;
import com.example.blockchainmonitorservice.dto.kafkamessage.Transaction;
import com.example.blockchainmonitorservice.dto.request.BtcRequestDto;
import com.example.blockchainmonitorservice.dto.response.BtcRawResponseDto;
import com.example.blockchainmonitorservice.dto.response.LatestBlockResponseDto;
import com.example.blockchainmonitorservice.dto.response.TransactionResponseDto;
import com.example.blockchainmonitorservice.entity.Address;
import com.example.blockchainmonitorservice.entity.ScanningProcess;
import com.example.blockchainmonitorservice.repository.AddressDao;
import com.example.blockchainmonitorservice.repository.ScanningProcessRepository;
import com.example.blockchainmonitorservice.service.BtcService;
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
    private AddressDao addressDao;

    private String chain = "btc";

    @Override
    public boolean scanningProcess(Long fromBlock, Long toBlock) {
        if(!isScanningProcess()) {
            Long latestBlockHeight = getLatestBlockHeight();
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

    public boolean isScanningProcess() {
        boolean isScanning = scanningProcessRepository.existsByStatus(String.valueOf(ScanningProcessConstant.IN_PROGRESS));
        return isScanning;
    }

    public long getLatestBlockHeight(){
        String url = "latest";
        BtcRawResponseDto<LatestBlockResponseDto> latestBlockResponseDto = latestblockdto.callExchange(new ParameterizedTypeReference<>() {},null, url);
        return latestBlockResponseDto.getResult().getBlockNumber();
    }

    public void getTransactionByBlockNumber(List<Long> listBlockNumber){
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
                sendMessage(transactionList);
            });
        });
    }

    public long insertScanProcess(Long fromBlock, Long toBlock) {
        ScanningProcess scanningProcess = new ScanningProcess();
        Timestamp timeStart = new Timestamp(System.currentTimeMillis());
        scanningProcess.setStartAt(timeStart);
        System.out.println(timeStart);
        scanningProcess.setFromBlock(fromBlock);
        scanningProcess.setChain(chain);
        scanningProcess.setStatus(String.valueOf(ScanningProcessConstant.IN_PROGRESS));
        scanningProcess.setToBlock(toBlock);
        scanningProcessRepository.save(scanningProcess);
        System.out.println(scanningProcess);
        //exception
        return scanningProcess.getId();
    }

    //Tra ve null hoac address
    public List<Address> getAddressListAndValue() {
        String addressString = "btc_29f18c747ca1b29914605a3ca0ea0966944156d245335b1bad1d14ad18b6e2a61";
        List<Address> addressList = new ArrayList<>();
        Address address = addressDao.findAddressByKey(addressString);
        if (address != null)
        {
            addressList.add(address);
        }
        return addressList;
    }

    //Tra ve null hoac value
    public String checkAddressValue(String addressString) {
        String value = addressDao.findValueByKey(addressString);
        if (value != null)
        {
            return value;
        }
        return null;
    }

    public boolean updateProcessEvent(Long processId) {
        ScanningProcess scanningProcess = scanningProcessRepository.getReferenceById(processId);
        scanningProcess.setStatus(String.valueOf(ScanningProcessConstant.COMPLETE));
        Timestamp timeEnd = new Timestamp(System.currentTimeMillis());
        scanningProcess.setEndAt(timeEnd);
        scanningProcessRepository.save(scanningProcess);
        return true;
    }

    public List<Transaction> getTransactionHaveAddress(Map<String,List<String>> listTransaction) {
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

    public void sendMessage(List<Transaction> transactionList) {
        transactionList.forEach((element) -> {
            if(element.getValue().equals(String.valueOf(ScanningProcessConstant.DEPOSIT)))
            {
                System.out.println("DEPOSIT:" +element);
            } else {
                System.out.println("WITHDRAW:" +element);
            }
        });
    }
}
