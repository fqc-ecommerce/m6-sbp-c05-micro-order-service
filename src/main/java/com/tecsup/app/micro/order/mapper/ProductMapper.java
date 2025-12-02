package com.tecsup.app.micro.order.mapper;

import com.tecsup.app.micro.order.client.User;
import com.tecsup.app.micro.order.dto.Product;
import com.tecsup.app.micro.order.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "createdByUser", ignore = true) // Will be set in toDomainWithUser
    Product toDomain(ProductEntity entity);

    @Mapping(target = "createdBy", source = "createdByUser.id")
    ProductEntity toEntity(Product domain);

    default Product toDomainWithUser(ProductEntity productEntity, User user){
        Product product = toDomain(productEntity);
        product.setCreatedByUser(user);
        return product;
    }
}
