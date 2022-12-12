package com.rsv.orderservice.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="t_orders")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;
    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderLineItems> orderLineItemsList;
}