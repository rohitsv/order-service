package com.rsv.orderservice.service;

import com.rsv.orderservice.dto.InventoryResponseDto;
import com.rsv.orderservice.dto.OrderLineItemsDto;
import com.rsv.orderservice.dto.OrderRequestDto;
import com.rsv.orderservice.dto.OrderResponseDto;
import com.rsv.orderservice.model.Order;
import com.rsv.orderservice.model.OrderLineItems;
import com.rsv.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    @Autowired
    private WebClient webClient;
    @Autowired
    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequestDto orderRequest) {
        List<String> skuCodes = orderRequest.getOrderLineItemsDtoList()
                        .stream()
                        .map(OrderLineItemsDto::getSkuCode)
                        .toList();

        InventoryResponseDto[] inventoryResponseDtos = webClient.get()
                .uri("http://localhost:8082/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCodes", skuCodes)
                                .build()
                )
                .retrieve()
                .bodyToMono(InventoryResponseDto[].class)
                .block();

        boolean isInStock = Arrays.stream(inventoryResponseDtos).allMatch(InventoryResponseDto::isInStock);

        if(isInStock) {
            Order order = mapToOrder(orderRequest);
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("One or more products are not in stock");
        }

    }

    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToOrderResponseDto)
                .toList();
    }

    private Order mapToOrder(OrderRequestDto orderRequest) {
        return Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderLineItemsList(mapToOrderLineItemsList(orderRequest.getOrderLineItemsDtoList()))
                .build();
    }

    private List<OrderLineItems> mapToOrderLineItemsList(List<OrderLineItemsDto> orderLineItemsDtoList) {
        return orderLineItemsDtoList.stream()
                .map(this::mapToOrderLineItems)
                .toList();
    }

    private OrderLineItems mapToOrderLineItems(OrderLineItemsDto orderLineItemsDto) {
        return OrderLineItems.builder()
                .price(orderLineItemsDto.getPrice())
                .skuCode(orderLineItemsDto.getSkuCode())
                .quantity(orderLineItemsDto.getQuantity())
                .build();
    }

    private OrderResponseDto mapToOrderResponseDto(Order order) {
        return OrderResponseDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderLineItemsDtoList(mapToOrderLineItemsDtoList(order.getOrderLineItemsList()))
                .build();
    }

    private List<OrderLineItemsDto> mapToOrderLineItemsDtoList(List<OrderLineItems> orderLineItemsList) {
        return orderLineItemsList.stream()
                .map(this::mapToOrderLineItemsDto)
                .toList();
    }

    private OrderLineItemsDto mapToOrderLineItemsDto(OrderLineItems orderLineItems) {
        return OrderLineItemsDto.builder()
                .id(orderLineItems.getId())
                .skuCode(orderLineItems.getSkuCode())
                .price(orderLineItems.getPrice())
                .quantity(orderLineItems.getQuantity())
                .build();
    }
}
