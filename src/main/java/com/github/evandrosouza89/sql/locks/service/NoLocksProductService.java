package com.github.evandrosouza89.sql.locks.service;

import com.github.evandrosouza89.sql.locks.model.Product;
import com.github.evandrosouza89.sql.locks.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class NoLocksProductService {

    private final ProductRepository repository;

    public Product findById(final long productId) {
        return repository.findById(productId).orElseThrow();
    }

    public Product persist(final Product product) {
        return repository.save(product);
    }

}
