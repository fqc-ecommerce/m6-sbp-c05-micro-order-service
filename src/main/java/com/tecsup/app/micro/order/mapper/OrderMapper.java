package com.tecsup.app.micro.order.mapper;

import com.tecsup.app.micro.order.client.User;
import com.tecsup.app.micro.order.dto.*;
import com.tecsup.app.micro.order.entity.OrderEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "userId", ignore = true)
    OrderEntity toEntity(Order order);
    
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "orderNumber", source = "entity.orderNumber")
    @Mapping(target = "status", source = "entity.status")
    @Mapping(target = "totalAmount", source = "entity.totalAmount")
    @Mapping(target = "createdAt", source = "entity.createdAt")
    @Mapping(target = "updatedAt", source = "entity.updatedAt")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "items", source = "items")
    Order toOrder(OrderEntity entity, User user, List<OrderItem> items);
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    UserResponse toUserResponse(User user);
}
