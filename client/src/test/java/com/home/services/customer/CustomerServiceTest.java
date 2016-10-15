package com.home.services.customer;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.home.ClientApplication;
import com.home.Customer;
import com.home.factory.StorageNodeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by Amarendra Kumar on 10/10/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ClientApplication.class})
public class CustomerServiceTest {

    @Autowired
    CustomerService customerService;

    @Autowired
    @Qualifier("ClientInstance")
    HazelcastInstance hazelcastInstance;

    @Autowired
    StorageNodeFactory storageNodeFactory;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/mm/dd");


    @Before
    public void tearDown(){
        hazelcastInstance.getMap(CustomerService.CUSTOMERS_MAP).clear();
    }

    @Test
    public void testAddCustomer() throws InterruptedException {
        storageNodeFactory.ensureClusterSize(1);
        Customer customer = new Customer(1L, "Amar" , new Date(), "amar@gmail.com");
        customerService.addCustomer(customer);

        IMap<Long, Customer> customerMap = hazelcastInstance.getMap(CustomerService.CUSTOMERS_MAP);
        assertEquals(1, customerMap.size());
        assertEquals(customer, customerMap.get(1L));
    }

    @Test
    public void testAddCustomers() throws InterruptedException {
        storageNodeFactory.ensureClusterSize(1);
        Customer customer = new Customer(1L, "Amar" , new Date(), "amar@gmail.com");
        Customer customer2 = new Customer(2L, "Amar2" , new Date(), "amar2@gmail.com");
        Customer customer3 = new Customer(3L, "Amar3" , new Date(), "amar3@gmail.com");

        List<Customer> customers = Arrays.asList(customer, customer2, customer3);
        customerService.addCustomers(customers);

        IMap<Long, Customer> customerMap = hazelcastInstance.getMap(CustomerService.CUSTOMERS_MAP);
        assertEquals(3, customerMap.size());
        assertEquals(customer, customerMap.get(1L));
        assertEquals(customer2, customerMap.get(2L));
        assertEquals(customer3, customerMap.get(3L));
    }

    @Test
    public void testNoDataLossWithOnlyOneNode() throws InterruptedException, ParseException {
        storageNodeFactory.ensureClusterSize(4);

        int maxCustomers = 1000;
        List<Customer> customers = generateCustomers(maxCustomers);
        customerService.addCustomers(customers);

        IMap<Long, Customer> customerMap = hazelcastInstance.getMap(CustomerService.CUSTOMERS_MAP);
        assertEquals(maxCustomers, customerMap.size());
        storageNodeFactory.ensureClusterSize(1);
        assertEquals(maxCustomers, customerMap.size());

    }

    private List<Customer> generateCustomers(int maxCustomers) throws ParseException {
        List<Customer> customers = new ArrayList<>();


        Date date = simpleDateFormat.parse("1980/01/01");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        for(long x = 0; x < maxCustomers ; x++){
            customers.add(new Customer(x, "Customer"+x , calendar.getTime(), "Customer"+x + "@gmail.com"));
            calendar.add(Calendar.YEAR, 1);
        }
        return customers;
    }

    @Test
    public void testNoDataLossAfterClusterShutdown() throws InterruptedException, ParseException {
        storageNodeFactory.ensureClusterSize(4);

        int maxCustomers = 1000;
        List<Customer> customers = generateCustomers(maxCustomers);
        customerService.addCustomers(customers);

        IMap<Long, Customer> customerMap = hazelcastInstance.getMap(CustomerService.CUSTOMERS_MAP);
        assertEquals(maxCustomers, customerMap.size());
        storageNodeFactory.ensureClusterSize(0); // Shutdown all storage nodes

        storageNodeFactory.ensureClusterSize(3); //Start another 3 storage nodes
        IMap<Long, Customer> customerMap1 = hazelcastInstance.getMap(CustomerService.CUSTOMERS_MAP);
        assertEquals(maxCustomers, customerMap1.size());
    }

    @Test
    public void testSearchCustomerWithDob() throws ParseException {
        customerService.addCustomers(generateCustomers(10));

        Date startDate = simpleDateFormat.parse("1980/01/01");
        Date endDate = simpleDateFormat.parse("1982/01/01");

        Collection<Customer> customers = customerService.findCustomer(startDate, endDate);
        assertEquals(2, customers.size());
    }

    @Test
    public void testFindCustomersByEmail() throws ParseException {
        customerService.addCustomers(generateCustomers(10));

        Collection<Customer> customersByEmail = customerService.findCustomersByEmail("Customer0@gmail.com");
        assertEquals(1, customersByEmail.size());
        List<Customer> customers = generateCustomers(1);
        assertEquals(customers.get(0), customersByEmail.iterator().next());
        System.out.printf("");
    }
}
