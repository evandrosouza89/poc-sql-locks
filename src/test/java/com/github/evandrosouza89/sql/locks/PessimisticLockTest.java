package com.github.evandrosouza89.sql.locks;

import com.github.evandrosouza89.sql.locks.model.Product;
import com.github.evandrosouza89.sql.locks.service.PessimisticLockProductService;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@SpringBootTest
class PessimisticLockTest {

    private static final int INITIAL_QUANTITY = 10;

    private static final int CONCURRENT_CALLS = 10;

    private static final int EXPECTED_FINAL_QUANTITY = INITIAL_QUANTITY - CONCURRENT_CALLS;

    @Autowired
    private PessimisticLockProductService service;

    @Nested
    @Order(1)
    class ConcurrentDatabaseCallsSimulation {

        @Transactional
        @Commit
        @Execution(ExecutionMode.CONCURRENT)
        @RepeatedTest(value = CONCURRENT_CALLS, name = "Concurrent execution {currentRepetition}")
        void productConsumptionTest() {

            // Given: We have a product on which we want to increment its price

            final Product product = service.findByIdUsingPessimisticLocking(1L);

            final Product updatedProduct = product.toBuilder()
                    .quantity(product.getQuantity() - 1).build();


            // When : We persist this product

            final Product persistedProduct = service.merge(updatedProduct);


            // Then : Verify a product was persisted

            assertThat(persistedProduct).isNotNull();

        }
    }

    @Nested
    @Order(2)
    class CheckProductFinalState {

        @Test
        void finalQuantityTest() {

            // Given: a product has undergone concurrent quantity reduction previously.
            final Product product = service.findById(1L);

            // Then : Ensure that the price adjustment has been executed accurately.
            assertThat(product.getQuantity()).isEqualTo(EXPECTED_FINAL_QUANTITY);

        }
    }

}