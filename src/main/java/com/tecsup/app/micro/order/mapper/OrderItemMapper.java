package com.tecsup.app.micro.order.mapper;

import com.tecsup.app.micro.order.client.Product;
import com.tecsup.app.micro.order.dto.OrderItem;
import com.tecsup.app.micro.order.dto.ProductResponse;
import com.tecsup.app.micro.order.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);

    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "unitPrice", source = "unitPrice")
    @Mapping(target = "subtotal", source = "subtotal")
    OrderItemEntity toEntity(OrderItem orderItem);
    
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "quantity", source = "entity.quantity")
    @Mapping(target = "unitPrice", source = "entity.unitPrice")
    @Mapping(target = "subtotal", source = "entity.subtotal")
    @Mapping(target = "product", source = "product", qualifiedByName = "toProductResponse")
    OrderItem toDTO(OrderItemEntity entity, Product product);
    
    @Named("toProductResponse")
    default ProductResponse toProductResponse(Product product) {
        if (product == null) {
            return null;
        }
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getPrice()
        );
    }
}
