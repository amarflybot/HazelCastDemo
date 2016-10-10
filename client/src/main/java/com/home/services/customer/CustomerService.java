package com.home.services.customer;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.home.Customer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Amarendra Kumar on 10/10/2016.
 */
@Service
public class CustomerService implements MapNames{

    private HazelcastInstance hazelcastInstance;
    private IMap<Long, Customer> customersMap;

    public CustomerService(@Qualifier("ClientInstance") HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @PostConstruct
    public void init(){
        customersMap = hazelcastInstance.getMap(CUSTOMERS_MAP);
    }

    public void addCustomer(Customer customer){
        customersMap.put(customer.getId(),customer);
    }

    public void addCustomers(Collection<Customer> customers){
        Map<Long,Customer> customersLocalMap = new HashMap<>();
        for (Customer customer : customers){
            customersLocalMap.put(customer.getId(), customer);
        }
        customersMap.putAll(customersLocalMap);
    }

    public void updateCustomer(Customer customer){
        customersMap.put(customer.getId(), customer);
    }

    public void deleteCustomer(Customer customer){
        customersMap.delete(customer.getId());
    }
}
