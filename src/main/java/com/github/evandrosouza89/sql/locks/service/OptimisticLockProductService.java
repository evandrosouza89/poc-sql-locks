package com.github.evandrosouza89.sql.locks.service;

import com.github.evandrosouza89.sql.locks.model.VersionedProduct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class OptimisticLockProductService {

    private final EntityManager entityManager;

    public VersionedProduct findById(final long productId) {
        return entityManager.find(VersionedProduct.class, productId);
    }

    @Transactional
    public VersionedProduct findByIdUsingOptimisticLocking(final long productId) {
        return entityManager.find(VersionedProduct.class, productId, LockModeType.OPTIMISTIC);
    }

    @Transactional
    public VersionedProduct merge(final VersionedProduct product) {

        entityManager.merge(product);

        return product;

    }

}
