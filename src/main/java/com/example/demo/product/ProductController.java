package com.example.demo.product;

import com.example.demo.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/v1/product")
public class ProductController {

    private static final String UPLOAD_DIR = "src/web/images/";

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
    private Product save(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") int price,
            @RequestParam("status") boolean status,
            @RequestPart("file") MultipartFile file
    ) {
        validRole();

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStatus(status);

        if (!file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
            File dir = new File(UPLOAD_DIR+fileName);

            if(dir.exists()){
                System.out.println("EXIST");
            }

            Path path = Path.of(UPLOAD_DIR+fileName);

            try{
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("CREATED");

                System.out.println(path.toString());
                product.setUrlImage(fileName);
            } catch (Exception e){
                System.out.println("Error");
                System.out.println(e.getMessage());
            }
        }

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


    @GetMapping("/images/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable("fileName") String fileName) {
        File file = new File(UPLOAD_DIR + fileName);

        if (!file.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
        }

        String mimeType = null;
        try {
            mimeType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .body(resource);
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
