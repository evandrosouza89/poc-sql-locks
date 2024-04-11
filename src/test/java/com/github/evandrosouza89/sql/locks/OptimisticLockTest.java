package com.github.evandrosouza89.sql.locks;

import com.github.evandrosouza89.sql.locks.model.VersionedProduct;
import com.github.evandrosouza89.sql.locks.service.OptimisticLockProductService;
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
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@SpringBootTest
class OptimisticLockTest {

    private static final int INITIAL_QUANTITY = 10;

    private static final int CONCURRENT_CALLS = 10;

    private static final int EXPECTED_FINAL_QUANTITY = INITIAL_QUANTITY - 1;

    @Autowired
    private OptimisticLockProductService service;

    @Nested
    @Order(1)
    class ConcurrentDatabaseCallsSimulation {

        @Transactional
        @Execution(ExecutionMode.CONCURRENT)
        @RepeatedTest(value = CONCURRENT_CALLS, name = "Concurrent execution {currentRepetition}")
        void productConsumptionTest() {

            // Given: We have a product on which we want to increment its price

            final VersionedProduct product = service.findByIdUsingOptimisticLocking(1L);

            final VersionedProduct updatedProduct = product.toBuilder()
                    .quantity(product.getQuantity() - 1).build();


            // When : We persist this product

            try {

                final VersionedProduct persistedProduct = service.merge(updatedProduct);


                // Force transaction to commit. If @Commit annotation is used then exception will not be caught.

                TestTransaction.flagForCommit();

                TestTransaction.end();

                // Then : Verify a product was persisted

                assertThat(persistedProduct).isNotNull();

            } catch (final Exception e) {

                // Then : Or an OptimisticLockException exception is thrown
                assertThat(e).isInstanceOf(ObjectOptimisticLockingFailureException.class);

                throw e;

            }

        }
    }

    @Nested
    @Order(2)
    class CheckProductFinalState {

        @Test
        void finalQuantityTest() {

            // Given: a product has undergone concurrent quantity reduction previously.
            final VersionedProduct product = service.findById(1L);

            // Then : Ensure that the price adjustment has been executed accurately.
            assertThat(product.getQuantity()).isEqualTo(EXPECTED_FINAL_QUANTITY);

        }

    }

}