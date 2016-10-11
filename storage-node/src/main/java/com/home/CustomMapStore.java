package com.home;

import com.hazelcast.core.MapStore;
import com.home.dao.CustomerDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by V691139 on 10/10/2016.
 */
public class CustomMapStore implements MapStore<Long, Customer>{

    @Autowired
    private CustomerDao customerDao;

    @Override
    public void store(Long key, Customer value) {
        customerDao.save(value);
    }

    @Override
    public void storeAll(Map<Long, Customer> map) {
        map.forEach((k,v)->{
            customerDao.save(v);
        });
    }

    @Override
    public void delete(Long key) {
        customerDao.delete(key);
    }

    @Override
    public void deleteAll(Collection<Long> keys) {
        keys.forEach(k->{
            customerDao.delete(k);
        });
    }

    @Override
    public Customer load(Long key) {
        return customerDao.findOne(key);
    }

    @Override
    public Map<Long, Customer> loadAll(Collection<Long> keys) {
        Map<Long, Customer> map = new HashMap<>();
        keys.forEach(k->{
            Customer customer = customerDao.findOne(k);
            map.put(customer.getId(), customer);
        });
        return map;
    }

    @Override
    public Iterable<Long> loadAllKeys() {
        List<Customer> customers = customerDao.findAll();
        return StreamSupport.stream(customers.spliterator(),false)
                .map(Customer::getId)
                .collect(Collectors.toList());
    }
}
