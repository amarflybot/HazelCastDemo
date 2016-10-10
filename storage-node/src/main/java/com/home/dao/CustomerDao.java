package com.home.dao;

import com.home.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by V691139 on 10/10/2016.
 */
public interface CustomerDao extends JpaRepository<Customer, Long> {
}
