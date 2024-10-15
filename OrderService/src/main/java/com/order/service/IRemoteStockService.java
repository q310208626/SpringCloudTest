package com.order.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="stock-service")
public interface IRemoteStockService {

    @GetMapping("/stock/addProductStock")
    String addProductStock(@RequestParam("productId") int productId, @RequestParam("num") int num);

    @GetMapping("/stock/reduceProduct")
    String reduceProduct(@RequestParam("productId") String productId, @RequestParam("num") int num);
}
