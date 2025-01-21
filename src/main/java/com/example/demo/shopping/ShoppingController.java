package com.example.demo.shopping;

import com.example.demo.entity.JwtAuthenticationDetails;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderRequest;
import com.example.demo.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/v1/shopping")
public class ShoppingController {

    Logger logger = LoggerFactory.getLogger(ShoppingController.class);

    @Autowired
    private ShoppingService shoppingService;

    @GetMapping("{orderId}")
    private Optional<Order> getProfile(@PathVariable("orderId") Long orderId) {
        Optional<Order> order = shoppingService.findById(orderId);
        if (order.isPresent()) {
            return order;
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }
    }

    @PostMapping
    private Order save(@RequestBody OrderRequest orderRequest) {
        return shoppingService.saveOrUpdate(getId(), orderRequest.getProductIds(), orderRequest.getQuantities());
    }

    @PutMapping
    private Order update(@RequestBody List<Long> productIds, @RequestBody List<Integer> quantities) {
        return shoppingService.saveOrUpdate(getId(), productIds, quantities);
    }

    @DeleteMapping("{orderId}")
    private void delete(@PathVariable("orderId") Long orderId) {
        shoppingService.deleteById(orderId);
    }


    private Long getId() {
        JwtAuthenticationDetails details = (JwtAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return details.getUserId();
    }
}
