package io.spring.demo.customer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Optional<Customer> findCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Transactional
    public Customer createCustomer(CustomerDto customerDto) {
        final var customer = new Customer();
        customer.setName(customerDto.name());
        customer.setEmail(customerDto.email());
        return customerRepository.saveAndFlush(customer);
    }
}
