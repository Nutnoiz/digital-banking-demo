package com.unb.digitalbanking.repository;

import com.unb.digitalbanking.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByCustomerNo(String customerNo);

    Optional<Customer> findByEmail(String email);
}