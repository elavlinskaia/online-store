package com.lavlinskaia.orderservice.service;

import java.util.List;
import java.util.UUID;
import java.util.Arrays;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.lavlinskaia.orderservice.dto.InventoryResponse;
import com.lavlinskaia.orderservice.dto.OrderLineItemsDto;
import com.lavlinskaia.orderservice.dto.OrderRequest;
import com.lavlinskaia.orderservice.model.Order;
import com.lavlinskaia.orderservice.model.OrderLineItems;
import com.lavlinskaia.orderservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
            .map(OrderLineItems::getSkuCode)
            .toList();

        // вызвать Inventory Service и разместить заказ, если продукт в наличии
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                 .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                 .retrieve()
                 .bodyToMono(InventoryResponse[].class)
                 .block(); // для синхронного взимодействия

        boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                    .allMatch(InventoryResponse::isInStock);
        
        if (allProductsInStock) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Продукта нет в продаже");
        }
        
    }
    
    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
