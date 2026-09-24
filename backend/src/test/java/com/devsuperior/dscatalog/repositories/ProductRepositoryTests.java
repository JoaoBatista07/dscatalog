package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;

    private long existingId;
    private long nonExistingId;
    private long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {

        long existingId = 1L;

        repository.deleteById(existingId);
        Optional<Product> result = repository.findById(existingId);

        Assertions.assertFalse(result.isPresent()); //verifica se é false, ou seja, se o objeto não está presente no banco de dados
    }

    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIsNull(){

        Product product = Factory.createProduct();
        product.setId(null);

        repository.save(product);

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProducts+1, product.getId());
    }

    @Test
    public void findByIdShouldReturnProductNotNullWhenExistingId(){

        Optional<Product> result = repository.findById(existingId);

        Assertions.assertNotNull(result);
    }

    @Test
    public void findByIdShouldReturnNullProductWhenIdNotExisting(){

        Optional<Product> result = repository.findById(nonExistingId);

        Assertions.assertTrue(result.isEmpty());
    }



}

