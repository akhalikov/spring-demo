package io.spring.demo.customer;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> customerById(@PathVariable Long id) {
        return customerService.findCustomerById(id)
            .map(customer -> ResponseEntity.ok().body(customer))
            .orElseThrow(() -> new EntityNotFoundException("Could not find customer with id=%s".formatted(id)));
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody CustomerDto customerDto) {
        return ResponseEntity
            .ok()
            .body(customerService.createCustomer(customerDto));
    }
}
