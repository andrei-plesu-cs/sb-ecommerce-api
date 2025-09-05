package com.andrei.plesoianu.sbecom.config;

import com.andrei.plesoianu.sbecom.model.Cart;
import com.andrei.plesoianu.sbecom.payload.CartDto;
import com.andrei.plesoianu.sbecom.payload.ProductDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    ModelMapper modelMapper() {
        var mapper = new ModelMapper();

        TypeMap<Cart, CartDto> typeMap = mapper.createTypeMap(Cart.class, CartDto.class);
        typeMap.addMappings(m -> m.skip(CartDto::setProducts));
        typeMap.setPostConverter(context -> {
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

        return mapper;
    }
}
