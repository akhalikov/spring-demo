package io.spring.demo.customer;

import io.spring.demo.customer.request.CreateCustomerRequest;
import io.spring.demo.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer getCustomer(Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Could not find customer with id=%s", id));
    }

    @Transactional
    public Customer createCustomer(CreateCustomerRequest request) {
        final var customer = new Customer();
        customer.setName(request.name());
        customer.setEmail(request.email());
        return customerRepository.saveAndFlush(customer);
    }
}
