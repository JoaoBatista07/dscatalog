package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

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

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
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
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false); // 🔧 ajuste explícito

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
        Mockito.verify(repository, Mockito.times(1)).existsById(nonExistingId);
    }

    @Test
    public void deleteShouldThrowDataIntegrityViolationExceptionWhenIdIsDependent() {

        Mockito.when(repository.existsById(dependentId)).thenReturn(true);

        Mockito.doThrow(DataIntegrityViolationException.class)
                .when(repository).deleteById(dependentId);

        Assertions.assertThrows(DatabaseException.class, () -> service.delete(dependentId));
        Mockito.verify(repository, Mockito.times(1)).existsById(dependentId);
    }
}
