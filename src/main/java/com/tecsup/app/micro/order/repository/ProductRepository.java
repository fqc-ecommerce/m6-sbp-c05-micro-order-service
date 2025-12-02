package com.tecsup.app.micro.order.repository;

import com.tecsup.app.micro.order.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

}
