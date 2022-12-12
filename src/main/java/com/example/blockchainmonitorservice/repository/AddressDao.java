package com.example.blockchainmonitorservice.repository;


import com.example.blockchainmonitorservice.entity.Address;

public interface AddressDao {
    String findValueByKey(String id);
    Address findAddressByKey(String key);
}
