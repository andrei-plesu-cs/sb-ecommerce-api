package com.andrei.plesoianu.sbecom.config;

import com.andrei.plesoianu.sbecom.model.Cart;
import com.andrei.plesoianu.sbecom.model.OrderItem;
import com.andrei.plesoianu.sbecom.payload.cart.CartDto;
import com.andrei.plesoianu.sbecom.payload.order.OrderItemDto;
import com.andrei.plesoianu.sbecom.payload.product.ProductDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    ModelMapper modelMapper() {
        var mapper = new ModelMapper();

        mapper.createTypeMap(Cart.class, CartDto.class)
                .addMappings(m -> m.skip(CartDto::setProducts))
                .setPostConverter(context -> {
                   Cart source = context.getSource();
                   CartDto destination = context.getDestination();

                   destination.setProducts(source.getCartItems().stream()
                           .map(item -> {
                                ProductDto dto = mapper.map(item.getProduct(), ProductDto.class);
                                dto.setQuantity(item.getQuantity());
                                return dto;
                            })
                           .toList()
                   );

                   return destination;
                });

        // OrderItem -> OrderItemDto mapping
        mapper.addMappings(new PropertyMap<OrderItem, OrderItemDto>() {
            @Override
            protected void configure() {
                // This disables the auto-match for productId and forces only our mapping
                map().setProductId(source.getProductId());
            }
        });

        return mapper;
    }
}
