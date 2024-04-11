package com.github.evandrosouza89.sql.locks.repository;

import com.github.evandrosouza89.sql.locks.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}