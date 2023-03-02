package com.springboot.demo.controllers;

import com.springboot.demo.models.Product;
import com.springboot.demo.models.ResponseObject;
import com.springboot.demo.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.support.Repositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(path = "api/v1/Products")


public class ProductController {
    //DI = Dependency Injection
    @Autowired
    private ProductRepository repository;


    @GetMapping("")
        // http://localhost:8080/api/v1/Products
    List<Product> getallProducts() {
        return repository.findAll(); // get all product
       /*return List.of(new Product(1L,"Macbook",2019,1000.0,""),
               new Product(1L,"Iphone 11",2019,500.0,""));*/
    }

    @GetMapping("/{id}")
    ResponseEntity<ResponseObject> findById(@PathVariable Long id) {
        Optional<Product> foundProduct = repository.findById(id);
        return foundProduct.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("ok", "Tim duoc san pham thanh cong", foundProduct)
                ) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed", "Khong tim duoc san pham voi id = " + id, "")
                );
    }

    @PostMapping("/insert")
    ResponseEntity<ResponseObject> insertProduct(@RequestBody Product newProduct) {
        List<Product> foundProducts = repository.findByProductName(newProduct.getProductName().trim());
        return (foundProducts.size() > 0) ?
                ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                        new ResponseObject("failed", "Ten product bi trung", "")
                )
                :
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("ok", "Them Product thanh cong", repository.save(newProduct))
                );
    }
   //  update 1 product neu ko co thi insert
    @PutMapping("/{id}")
    ResponseEntity<ResponseObject> updateProduct(@RequestBody Product newProduct,@PathVariable Long id) {
        Product updatedProduct = repository.findById(id)
                .map(product -> {
                    product.setProductName(newProduct.getProductName());
                    product.setPrice(newProduct.getPrice());
                    product.setUrl(newProduct.getUrl());
                    product.setYear(newProduct.getYear());
                    return repository.save(product);
                }).orElseGet(()->{
                    newProduct.setId(id);
                    return repository.save(newProduct);
                });
        return ResponseEntity.status(HttpStatus.OK).body(
            new ResponseObject("ok","Update Product Thanh cong",updatedProduct)
        );
    }
    @DeleteMapping("/{id}")
    ResponseEntity<ResponseObject> deleteProduct(@PathVariable Long id) {
        boolean exists = repository.existsById(id);
         if (!exists) {
             return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                     new ResponseObject("failed", "Khong ton tai product co id = " + id, "")
             );
         }
         else {
             repository.deleteById(id);
             return ResponseEntity.status(HttpStatus.OK).body(
                     new ResponseObject("ok", "Xoa Product thanh cong", "")
             );
         }
    }


}
