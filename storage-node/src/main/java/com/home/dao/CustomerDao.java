package com.home.dao;

import com.home.Customer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * Created by V691139 on 10/10/2016.
 */
@Repository
public interface CustomerDao extends JpaRepository<Customer, Long> {
}
