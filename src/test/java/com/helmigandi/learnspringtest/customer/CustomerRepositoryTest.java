package com.helmigandi.learnspringtest.customer;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // We don't want the H2 in-memory database
class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository underTest;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        // Given
        UUID id = UUID.randomUUID();
        String phoneNumber = "082114772109";
        Customer sarah = new Customer(id, "Sarah", phoneNumber);

        // When
        underTest.save(sarah);

        // Then
        Optional<Customer> optionalCustomer = underTest.selectCustomerByPhoneNumber(phoneNumber);
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(cust -> {
                    assertThat(cust).usingRecursiveComparison().isEqualTo(sarah);
                });
    }

    @Test
    void itShouldSaveCustomer() {
        // Given
        UUID id = UUID.randomUUID();
        Customer sarah = new Customer(id, "Sarah", "082114772109");

        // When
        underTest.save(sarah);

        // Then
        Optional<Customer> optCustomer = underTest.findById(id);
        assertThat(optCustomer)
                .isPresent()
                .hasValueSatisfying(optionalCustomer -> {
//                    assertThat(optionalCustomer.getId()).isEqualTo(id);
//                    assertThat(optionalCustomer.getPhoneNumber()).isEqualTo("082114772109");
//                    assertThat(optionalCustomer.getName()).isEqualTo("Sarah");
                    assertThat(optionalCustomer).usingRecursiveComparison().isEqualTo(sarah);
                });
    }

    @Test
    void itShouldNotSelectCustomerWhenPhoneNumberDoesNotExist() {
        // Given
        String phoneNumber = "082147219982";

        // When
        Optional<Customer> optionalCustomer = underTest.selectCustomerByPhoneNumber(phoneNumber);

        // Then
        assertThat(optionalCustomer).isNotPresent();
    }

    @Test
    void itShouldNotSaveCustomerWhenNameIsNull() {
        // Given
        Customer customer = new Customer(UUID.randomUUID(), null, "0000");

        // When
        // Then
        assertThatThrownBy(() -> underTest.saveAndFlush(customer))
                .hasMessageContaining("must not be blank")
                .isInstanceOf(ConstraintViolationException.class);

//        ConstraintViolationException ex =
//                assertThrows(ConstraintViolationException.class, () -> underTest.saveAndFlush(customer));
//        assertNotNull(ex.getMessage());
//        assertTrue(ex.getMessage().contains("must not be blank"));
    }

    /**
     * 1. When you want to check Repository’s "save()" method, you must use it.
     * 2. When you want to check other methods of Repository (not including "save()"),
     * you must have data in database table, and you are NOT sure that Repository’s "save()" method is good or not,
     * then you use TestEntityManager for persisting data.
     */
    @Test
    void itShouldNotSaveCustomerWhenPhoneNumberIsNull() {
        // Given
        Customer customer = new Customer(UUID.randomUUID(), "Alex", null);

        // When
        underTest.save(customer);

        // Then
        assertThatThrownBy(() -> entityManager.flush())
                .hasMessageContaining("must not be blank")
                .isInstanceOf(ConstraintViolationException.class);

        // Or
        // assertThatThrownBy(() -> underTest.saveAndFlush(customer))
        //         .hasMessageContaining("must not be blank")
        //        .isInstanceOf(ConstraintViolationException.class);
    }
}