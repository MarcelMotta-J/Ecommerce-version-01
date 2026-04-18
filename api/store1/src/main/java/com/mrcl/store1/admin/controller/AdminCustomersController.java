package com.mrcl.store1.admin.controller;

import com.mrcl.store1.admin.dto.AdminCustomerEditResponse;
import com.mrcl.store1.admin.dto.AdminCustomerRow;
import com.mrcl.store1.admin.dto.AdminPagedResponse;
import com.mrcl.store1.dao.CustomerRepository;

import com.mrcl.store1.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/customers")public class AdminCustomersController {
    private final CustomerRepository customerRepository;

    public AdminCustomersController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // pagination
    @GetMapping
    public AdminPagedResponse<AdminCustomerRow> getCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<AdminCustomerRow> result = customerRepository.findAdminCustomers(pageable);

        return new AdminPagedResponse<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()
        );
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public void updateCustomer(
            @PathVariable Long id,
            @RequestBody Customer updatedCustomer
    ) {

        Customer customer = customerRepository.findById(id).orElseThrow();

        customer.setFirstName(updatedCustomer.getFirstName());
        customer.setLastName(updatedCustomer.getLastName());
        customer.setEmail(updatedCustomer.getEmail());

        customerRepository.save(customer);
    }

    @GetMapping("/{id}")
    public AdminCustomerEditResponse getCustomerById(@PathVariable Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow();



        return new AdminCustomerEditResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail()
        );
    }

}
