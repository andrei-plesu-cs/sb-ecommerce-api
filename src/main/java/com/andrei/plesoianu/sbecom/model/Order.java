package com.andrei.plesoianu.sbecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Email
    @Column(nullable = false)
    private String email;

    private LocalDate orderDate;

    private Double totalAmount;

    private String orderStatus;

    @OneToMany(mappedBy = "order",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE },
            orphanRemoval = true)
    @ToString.Exclude
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "address_id")
    @ToString.Exclude
    private Address address;

    @OneToOne
    @JoinColumn(name = "payment_id")
    @ToString.Exclude
    private Payment payment;
}
