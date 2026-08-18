package com.digitalbanking.account.service;

import com.digitalbanking.account.dto.CreateCustomerRequest;
import com.digitalbanking.account.dto.CustomerResponse;
import com.digitalbanking.account.entity.Customer;
import com.digitalbanking.account.exception.BusinessRuleException;
import com.digitalbanking.account.exception.ResourceNotFoundException;
import com.digitalbanking.account.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void shouldCreateCustomer() {

        Long userId = 1L;

        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setFirstName("Sai");
        request.setLastName("Teja");
        request.setPhoneNumber("9876543210");

        when(customerRepository.existsByUserId(userId))
                .thenReturn(false);

        Customer savedCustomer = new Customer();
        savedCustomer.setUserId(userId);
        savedCustomer.setFirstName("Sai");
        savedCustomer.setLastName("Teja");
        savedCustomer.setPhoneNumber("9876543210");

        when(customerRepository.save(any(Customer.class)))
                .thenReturn(savedCustomer);

        CustomerResponse response =
                customerService.createCustomer(userId, request);

        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals("Sai", response.getFirstName());
        assertEquals("Teja", response.getLastName());
        assertEquals("9876543210", response.getPhoneNumber());

        verify(customerRepository).existsByUserId(userId);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void shouldRejectDuplicateCustomer() {

        Long userId = 1L;

        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setFirstName("Sai");
        request.setLastName("Teja");
        request.setPhoneNumber("9876543210");

        when(customerRepository.existsByUserId(userId))
                .thenReturn(true);

        assertThrows(
                BusinessRuleException.class,
                () -> customerService.createCustomer(userId, request)
        );

        verify(customerRepository).existsByUserId(userId);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldGetCurrentCustomer() {

        Long userId = 1L;

        Customer customer = new Customer();
        customer.setUserId(userId);
        customer.setFirstName("Sai");
        customer.setLastName("Teja");
        customer.setPhoneNumber("9876543210");

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        CustomerResponse response =
                customerService.getCurrentCustomer(userId);

        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals("Sai", response.getFirstName());
        assertEquals("Teja", response.getLastName());
        assertEquals("9876543210", response.getPhoneNumber());

        verify(customerRepository).findByUserId(userId);
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {

        Long userId = 999L;

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.getCurrentCustomer(userId)
        );

        verify(customerRepository).findByUserId(userId);
    }
}