package com.rsv.orderservice.dto;

import com.rsv.orderservice.model.OrderLineItems;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderResponseDto {
    private Long id;
    private String orderNumber;
    private List<OrderLineItemsDto> orderLineItemsDtoList;
}