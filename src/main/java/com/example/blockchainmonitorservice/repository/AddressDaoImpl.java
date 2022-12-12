package com.example.blockchainmonitorservice.repository;

import com.example.blockchainmonitorservice.entity.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AddressDaoImpl implements AddressDao{

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String HASH_KEY = "AddressCoin";

    @Override
    public String findValueByKey(String key) {
        Object value = redisTemplate.opsForHash().get(HASH_KEY,key);
        if(value != null) {
            return value.toString();
        }
        return null;
    }

    @Override
    public Address findAddressByKey(String key) {
        Address address = new Address();
        String value = redisTemplate.opsForHash().get(HASH_KEY,key).toString();
        if(value != null) {
            address.setKey(key);
            address.setValue(value);
            return address;
        }
        else {
            return null;
        }
    }
}
