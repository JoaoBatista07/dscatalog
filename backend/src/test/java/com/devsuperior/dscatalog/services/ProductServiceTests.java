package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        product = Factory.createProduct();
        productDTO = Factory.createProductDTO();
    }

    @Test
    public void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(product));
        Mockito.when(repository.findAll(pageable)).thenReturn(page);

        Page<ProductDTO> result = service.findAllPaged(product.getName(), pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());
        Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.doNothing().when(repository).deleteById(existingId);

        Assertions.assertDoesNotThrow(() -> service.delete(existingId));
        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
        Mockito.verify(repository, Mockito.times(1)).existsById(nonExistingId);
    }

    @Test
    public void deleteShouldThrowDataIntegrityViolationExceptionWhenIdIsDependent() {
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);
        Mockito.doThrow(new DataIntegrityViolationException("integrity"))
               .when(repository).deleteById(dependentId);

        Assertions.assertThrows(DatabaseException.class, () -> service.delete(dependentId));
        Mockito.verify(repository, Mockito.times(1)).existsById(dependentId);
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenExistingId(){

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));

        productDTO = service.findById(existingId);

        Assertions.assertNotNull(productDTO);
        Assertions.assertEquals(product.getId(), productDTO.getId());

    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenNonExistingId(){

        Mockito.doThrow(new ResourceNotFoundException("Not Found"))
                .when(repository).findById(nonExistingId);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));

        Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);

    }

    @Test
    public void updateShouldReturnProductDTOWhenExistingId(){

        Category category = new Category(1L, "Electronics");

        Mockito.when(categoryRepository.getReferenceById(ArgumentMatchers.any()))
                .thenReturn(category);

        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(repository.save(product)).thenReturn(product);

        productDTO = service.update(product.getId(), productDTO);

        Assertions.assertNotNull(productDTO);
        Assertions.assertEquals(existingId, productDTO.getId());

    }
}
