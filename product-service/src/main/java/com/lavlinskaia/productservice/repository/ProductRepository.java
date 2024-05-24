package com.lavlinskaia.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.lavlinskaia.productservice.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
    
}
