package com.tecsup.app.micro.order.service;

import com.tecsup.app.micro.order.client.Product;
import com.tecsup.app.micro.order.client.ProductClient;
import com.tecsup.app.micro.order.client.User;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import com.tecsup.app.micro.order.client.UserClient;
import com.tecsup.app.micro.order.dto.*;
import com.tecsup.app.micro.order.entity.OrderEntity;
import com.tecsup.app.micro.order.entity.OrderItemEntity;
import com.tecsup.app.micro.order.mapper.OrderItemMapper;
import com.tecsup.app.micro.order.mapper.OrderMapper;
import com.tecsup.app.micro.order.repository.OrderItemRepository;
import com.tecsup.app.micro.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private UserClient userClient;
    @Mock
    private ProductClient productClient;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderService orderService;

    private CreateOrderRequest createOrderRequest;
    private Order order;
    private OrderEntity orderEntity;
    private User user;
    private Product product;
    private OrderItem orderItem;
    private OrderItemEntity orderItemEntity;


    @BeforeEach
    void setUp() {
        // Initialize test data
        user = User.builder()
                .id(1L)
                .name("Test User")
                .build();
        
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(new BigDecimal("10.00"))
                .build();
        
        orderEntity = new OrderEntity();
        orderEntity.setId(1L);
        orderEntity.setUserId(1L);
        orderEntity.setStatus("PENDING");
        orderEntity.setTotalAmount(new BigDecimal("20.00"));
        
        orderItemEntity = OrderItemEntity.builder()
                .id(1L)
                .orderId(1L)
                .productId(1L)
                .quantity(2)
                .unitPrice(new BigDecimal("10.00"))
                .subtotal(new BigDecimal("20.00"))
                .build();
                
        ProductResponse productResponse = new ProductResponse(1L, "Test Product", new BigDecimal("10.00"));
        orderItem = OrderItem.builder()
                .id(1L)
                .product(productResponse)
                .quantity(2)
                .unitPrice(new BigDecimal("10.00"))
                .subtotal(new BigDecimal("20.00"))
                .build();
        
        order = new Order();
        order.setId(1L);
        order.setStatus("PENDING");
        order.setTotalAmount(new BigDecimal("20.00"));
        order.setItems(List.of(orderItem));
        
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setUserId(1L);
        CreateOrderRequest.CreateOrderItemRequest itemRequest = new CreateOrderRequest.CreateOrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);
        createOrderRequest.setItems(List.of(itemRequest));
    }

    @Test
    void getOrderById_WhenOrderDoesNotExist_ShouldThrowException() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.getOrderById(1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Order not found with id: 1");
            
        verify(orderRepository).findById(1L);
        verifyNoInteractions(orderMapper);
        verifyNoInteractions(userClient);
    }
}