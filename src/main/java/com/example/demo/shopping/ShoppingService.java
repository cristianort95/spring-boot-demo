package com.example.demo.shopping;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderLine;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShoppingService {
    @Autowired
    ShoppingRepository orderRepository;
    @Autowired
    ProductRepository productRepository;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public Order saveOrUpdate(Long userId, List<Long> productIds, List<Integer> quantities) {
        if (productIds.size() != quantities.size()) {
            throw new IllegalArgumentException("El número de productos y cantidades debe ser el mismo");
        }

        // Buscar los productos por sus IDs
        List<Product> products = productRepository.findAllById(productIds);

        double totalAmount = 0;
        Order order = new Order();
        order.setUserId(userId);

        // Crear las OrderLines con las cantidades
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int quantity = quantities.get(i);

            OrderLine orderLine = new OrderLine();
            orderLine.setProduct(product);
            orderLine.setQuantity(quantity);
            orderLine.setOrder(order);  // Asociar con la orden

            // Calcular el precio total de la línea
            double lineTotal = product.getPrice() * quantity;
            orderLine.setTotalPrice(lineTotal);

            totalAmount += lineTotal; // Sumar el precio de esta línea al total de la orden

            // Agregar la OrderLine a la orden
            order.getOrderLines().add(orderLine);
        }

        order.setTotalAmount(totalAmount);  // Establecer el total de la orden

        return orderRepository.save(order);
    }
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }
}
