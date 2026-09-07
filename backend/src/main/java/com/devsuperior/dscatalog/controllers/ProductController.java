package com.devsuperior.dscatalog.controllers;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

    private ProductService ProductService;

    public ProductController(ProductService ProductService){
        this.ProductService = ProductService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> findAll(@RequestParam(name = "name", defaultValue = "") String name, Pageable pageable){
        Page<ProductDTO> page = ProductService.findAll(name, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id){
        ProductDTO dto = ProductService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> insertProduct(@RequestBody ProductDTO dto){
        dto = ProductService.insert(dto);
        URI uri = ServletUriComponentsBuilder //Pega a URL feita na requisição. Ex: http://localhost:8080/products
                .fromCurrentRequest().path("/{id}") //Cria um novo paramêtro na URL com o id. Ex: http://localhost:8080/products/{id}
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto){
        dto = ProductService.update(id, dto);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id){
        ProductService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
