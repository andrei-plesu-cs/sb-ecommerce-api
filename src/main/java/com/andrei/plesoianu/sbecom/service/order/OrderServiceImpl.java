package com.andrei.plesoianu.sbecom.service.order;

import com.andrei.plesoianu.sbecom.exceptions.ApiException;
import com.andrei.plesoianu.sbecom.exceptions.NotFoundException;
import com.andrei.plesoianu.sbecom.model.*;
import com.andrei.plesoianu.sbecom.payload.order.OrderDto;
import com.andrei.plesoianu.sbecom.payload.order.OrderRequestDto;
import com.andrei.plesoianu.sbecom.repositories.*;
import com.andrei.plesoianu.sbecom.service.cart.CartService;
import com.andrei.plesoianu.sbecom.util.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final AuthUtil authUtil;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(@NonNull OrderRepository orderRepository,
                            @NonNull AddressRepository addressRepository,
                            @NonNull PaymentRepository paymentRepository,
                            @NonNull OrderItemRepository orderItemRepository,
                            @NonNull ProductRepository productRepository,
                            @NonNull CartRepository cartRepository,
                            @NonNull CartService cartService,
                            @NonNull AuthUtil authUtil,
                            @NonNull ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.paymentRepository = paymentRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
        this.authUtil = authUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public OrderDto placeOrder(OrderRequestDto orderRequestDto, String paymentMethod) {
        var cart = cartService.getUserCart();

        var address = addressRepository.findById(orderRequestDto.getAddressId())
                .orElseThrow(() -> new NotFoundException(Address.class, orderRequestDto.getAddressId()));

        var order = new Order();
        order.setEmail(authUtil.loggedInEmail());
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Accepted");
        order.setAddress(address);

        var payment = new Payment();

        payment.setPaymentMethod(paymentMethod);
        payment.setPgName(orderRequestDto.getPgName());
        payment.setPgStatus(orderRequestDto.getPgStatus());
        payment.setPgPaymentId(orderRequestDto.getPgPaymentId());
        payment.setPgResponseMessage(orderRequestDto.getPgResponseMessage());
        payment.setOrder(order);
        var createdPayment = paymentRepository.save(payment);

        order.setPayment(createdPayment);
        var createdOrder = orderRepository.save(order);

        List<Product> productsToUpdate = new ArrayList<>();

        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new ApiException("Cart is empty");
        }
        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> {
                    var orderItem = new OrderItem();
                    orderItem.setProduct(item.getProduct());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setDiscount(item.getDiscount());
                    orderItem.setOrderedProductPrice(item.getProductPrice());
                    orderItem.setOrder(createdOrder);
                    order.getOrderItems().add(orderItem);

                    // also update the quantities on the product
                    var product = item.getProduct();
                    product.setQuantity(product.getQuantity() - item.getQuantity());
                    productsToUpdate.add(product);

                    return orderItem;
                })
                .toList();

        productRepository.saveAll(productsToUpdate);
        var createdOrderItems = orderItemRepository.saveAll(orderItems);

        // last, clear the cart
        cart.getCartItems().forEach(item -> item.setCart(null));
        cart.getCartItems().clear();
        cart.getUser().setCart(null);
        cart.setUser(null);
        cartRepository.deleteById(cart.getId());

        order.setOrderItems(createdOrderItems);
        return modelMapper.map(createdOrder, OrderDto.class);
    }

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .toList();
    }
}
