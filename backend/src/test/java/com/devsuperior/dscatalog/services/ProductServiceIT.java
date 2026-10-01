package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ProductServiceIT {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;


    @BeforeEach
    void setUp() {

        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;

    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExists() {

        productService.delete(existingId);

        Assertions.assertEquals(countTotalProducts-1, productRepository.count());

    }
    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nonExistingId);
        });

    }

    @Test
    public void findAllPagedShouldReturnPageWhenPage0Size10() {

        PageRequest pageRequest = PageRequest.of(0, 10); //Criando um pageable vindo da request

        Page<ProductDTO> result = productService.findAllPaged("", pageRequest); //chamando o metodo no service

        Assertions.assertFalse(result.isEmpty()); //testa se o pageable não é vazio
        Assertions.assertEquals(0, result.getNumber()); //testa se a pagina é realmente 0, conforme pageRequest
        Assertions.assertEquals(10, result.getSize()); //testa se a quantidade de registros em cada pagina é 10, conforme pageRequest
        Assertions.assertEquals(countTotalProducts, result.getTotalElements());//testa se a quantidade total de produtos no banco é igual ao total de elementos de todas as Paginas
    }

    @Test
    public void findAllPagedShouldEmptyPageWhenPageDoesNotExists() {

        PageRequest pageRequest = PageRequest.of(50, 10); //Criando um pageable vindo da request

        Page<ProductDTO> result = productService.findAllPaged("", pageRequest); //chamando o metodo no service

        Assertions.assertTrue(result.isEmpty()); //verifica se pagina inexistente retorna vazio

    }

    @Test
    public void findAllPagedShouldReturnSortedPageWhenSortByName() {

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name")); //Criando um pageable vindo da request com o paramêtro SortByName

        Page<ProductDTO> result = productService.findAllPaged("", pageRequest); //chamando o metodo no service

        Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName()); //testando se o primeiro elemento da lista ordenada é o Macbook
        Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
        Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
    }



}
