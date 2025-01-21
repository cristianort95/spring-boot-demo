package com.example.demo.product;

import com.example.demo.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/v1/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("")
    private List<Product> getProfile() {return productService.findAll();
    }

    @GetMapping("{productId}")
    private Optional<Product> getProfile(@PathVariable("productId") Long productId) {
        Optional<Product> product = productService.findById(productId);
        if (product.isPresent()) {
            return product;
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }
    }

    @PostMapping
    private Product save(@RequestBody Product product) {
        validRole();
        return productService.saveOrUpdate(product);
    }

    @PutMapping
    private Product update(@RequestBody Product product) {
        validRole();
        return productService.saveOrUpdate(product);
    }

    @DeleteMapping("{productId}")
    private void delete(@PathVariable("productId") Long productId) {
        validRole();
        productService.deleteById(productId);
    }


    private void validRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        if (!hasAdminRole) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permisos para acceder a este recurso");
        }
    }
}
