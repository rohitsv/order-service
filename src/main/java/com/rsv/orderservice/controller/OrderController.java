package com.rsv.orderservice.controller;

import com.rsv.orderservice.dto.OrderRequestDto;
import com.rsv.orderservice.dto.OrderResponseDto;
import com.rsv.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequestDto orderRequest) {
        orderService.placeOrder(orderRequest);
        return "Order placed successfully";
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponseDto> getAllOrders() {
        return orderService.getAllOrders();
    }

}
