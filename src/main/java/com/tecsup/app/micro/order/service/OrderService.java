package com.tecsup.app.micro.order.service;

import com.tecsup.app.micro.order.client.Product;
import com.tecsup.app.micro.order.client.ProductClient;
import com.tecsup.app.micro.order.client.User;
import com.tecsup.app.micro.order.client.UserClient;
import com.tecsup.app.micro.order.dto.*;
import com.tecsup.app.micro.order.dto.UserResponse;
import com.tecsup.app.micro.order.entity.OrderEntity;
import com.tecsup.app.micro.order.entity.OrderItemEntity;
import com.tecsup.app.micro.order.mapper.OrderItemMapper;
import com.tecsup.app.micro.order.mapper.OrderMapper;
import com.tecsup.app.micro.order.repository.OrderItemRepository;
import com.tecsup.app.micro.order.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserClient userClient;
    private final ProductClient productClient;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        log.info("Creating order for user: {}", request.getUserId());

        // Validar usuario
        User user = userClient.getUserById(request.getUserId());
        log.info("User validated: {}", user);

        // Calcular totales de los items
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // Crear la orden
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserId(request.getUserId());
        orderEntity.setStatus("PENDING");
        orderEntity.setTotalAmount(totalAmount);
        orderEntity.setCreatedAt(LocalDateTime.now());
        orderEntity.setUpdatedAt(LocalDateTime.now());
        orderEntity.setOrderNumber("TEMP-" + UUID.randomUUID()); // Valor temporal único

        // Guardar para obtener el ID
        orderEntity = orderRepository.save(orderEntity);
        log.info("Order saved with id: {}", orderEntity.getId());

        // Generar y actualizar con el número basado en el ID
        orderEntity.setOrderNumber(generateOrderNumber(orderEntity.getId()));
        orderEntity = orderRepository.save(orderEntity);

        // Crear los items de la orden
        for (CreateOrderRequest.CreateOrderItemRequest itemRequest : request.getItems()) {
            // Validar producto y obtener información
            Product product = productClient.getProductById(itemRequest.getProductId());
            log.info("Product validated: {}", product);

            // Calcular subtotal
            BigDecimal unitPrice = product.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
            totalAmount = totalAmount.add(subtotal);

            // Crear entity
            OrderItemEntity itemEntity = new OrderItemEntity();
            itemEntity.setOrderId(orderEntity.getId());
            itemEntity.setProductId(itemRequest.getProductId());
            itemEntity.setQuantity(itemRequest.getQuantity());
            itemEntity.setUnitPrice(unitPrice);
            itemEntity.setSubtotal(subtotal);

            itemEntity = orderItemRepository.save(itemEntity);
            log.info("Order item saved with id: {}", itemEntity.getId());

            // Usar el mapper para convertir la entidad a DTO
            OrderItem orderItem = orderItemMapper.toDTO(itemEntity, product);
            orderItems.add(orderItem);
        }

        // Actualizar total de la orden
        orderEntity.setTotalAmount(totalAmount);
        orderRepository.save(orderEntity);

        // Usar el mapper para construir la respuesta
        UserResponse userResponse = orderMapper.toUserResponse(user);
        return orderMapper.toOrder(orderEntity, user, orderItems);
    }

    public Order getOrderById(Long id) {
        log.info("Getting order by id: {}", id);

        OrderEntity orderEntity = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        // Obtener usuario
        User user = userClient.getUserById(orderEntity.getUserId());

        // Obtener items
        List<OrderItem> items = getOrderItemsByOrderId(id);

        // Usar el mapper para convertir la entidad a DTO
        return orderMapper.toOrder(orderEntity, user, items);
    }

    private String generateOrderNumber(Long orderId) {
        return "ORD-2025-" + String.format("%04d", orderId);
    }

    private List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        log.info("Getting order items for order id: {}", orderId);

        List<OrderItemEntity> itemEntities = orderItemRepository.findAll()
                .stream()
                .filter(item -> item.getOrderId().equals(orderId))
                .toList();

        return itemEntities.stream()
                .map(entity -> {
                    Product product = productClient.getProductById(entity.getProductId());
                    return orderItemMapper.toDTO(entity, product);
                })
                .collect(Collectors.toList());
    }
}
