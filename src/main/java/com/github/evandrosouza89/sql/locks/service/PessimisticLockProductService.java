package com.github.evandrosouza89.sql.locks.service;

import com.github.evandrosouza89.sql.locks.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class PessimisticLockProductService {

    private final EntityManager entityManager;

    public Product findById(final long productId) {
        return entityManager.find(Product.class, productId);
    }

    @Transactional
    public Product findByIdUsingPessimisticLocking(final long productId) {
        return entityManager.find(Product.class, productId, LockModeType.PESSIMISTIC_WRITE);
    }

    @Transactional
    public Product merge(final Product product) {

        entityManager.merge(product);

        return product;

    }

}
