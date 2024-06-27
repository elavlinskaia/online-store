package com.lavlinskaia.inventory_service.service;

import java.util.List;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lavlinskaia.inventory_service.dto.InventoryResponse;
import com.lavlinskaia.inventory_service.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    @SneakyThrows // не использовать в продакшене!!! - ловить исключение
    public List<InventoryResponse> isInStock(List<String> skuCode) {
//        log.info("Начало ожидания");
//        Thread.sleep(10000); // симулируем медленное поведение для теста таймлимита circuit-breaker
//        log.info("Ожидание окончено");
        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                .map(inventory -> InventoryResponse.builder()
                        .skuCode(inventory.getSkuCode())
                        .isInStock(inventory.getQuantity() > 0)
                        .build())
                .toList();
    }
}
