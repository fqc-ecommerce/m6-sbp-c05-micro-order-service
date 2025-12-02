package com.tecsup.app.micro.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.app.micro.order.dto.Order;
import com.tecsup.app.micro.order.dto.OrderItem;
import com.tecsup.app.micro.order.dto.ProductResponse;
import com.tecsup.app.micro.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@ActiveProfiles("test")
@SuppressWarnings("removal")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void getOrderById() throws Exception {
        // Given
        Order order = new Order();
        order.setId(1L);
        order.setOrderNumber("ORDER-123");
        order.setStatus("PENDING");
        order.setTotalAmount(new BigDecimal("1999.99"));
        
        ProductResponse productResponse = new ProductResponse(1L, "Test Product", new BigDecimal("1999.99"));
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setProduct(productResponse);
        orderItem.setQuantity(1);
        orderItem.setUnitPrice(new BigDecimal("1999.99"));
        orderItem.setSubtotal(new BigDecimal("1999.99"));
        
        order.setItems(Collections.singletonList(orderItem));
        
        // When
        when(orderService.getOrderById(1L)).thenReturn(order);
        
        // Then
        mockMvc.perform(get("/api/orders/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderNumber").value("ORDER-123"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(1999.99))
                .andExpect(jsonPath("$.items[0].id").value(1))
                .andExpect(jsonPath("$.items[0].product.id").value(1))
                .andExpect(jsonPath("$.items[0].product.name").value("Test Product"));
    }
}