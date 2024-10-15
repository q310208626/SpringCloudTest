package com.stock.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.stock.bean.TProductStock;
import com.stock.dao.TProductStockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

@Controller
public class StockController {

    @Autowired
    private TProductStockMapper tProductStockMapper;

    @ResponseBody
    @GetMapping("reduceProduct")
    public String reduce(@RequestParam("productId") String productId, @RequestParam("num") int num) {
        System.out.println("Current Time:" + LocalDateTime.now() + " Reduce Product:" + productId + " for:" + num);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "SUCCESS";
    }

    @ResponseBody
    @GetMapping("addProductStock")
    @SentinelResource(value = "stock")
    public String addProductStock(@RequestParam("productId") int productId, @RequestParam("num") int num) {
        TProductStock productStock = new TProductStock();
        productStock.setProductId(productId);
        productStock.setStockNum(num);
        tProductStockMapper.insert(productStock);
        return "SUCCESS";
    }

    @ResponseBody
    @GetMapping("addProductStockWithException")
    public String addProductStockWithException(@RequestParam("productId") int productId, @RequestParam("num") int num) {
        TProductStock productStock = new TProductStock();
        productStock.setProductId(productId);
        productStock.setStockNum(num);
        tProductStockMapper.insert(productStock);
        throw new RuntimeException("分布式事务测试异常");
    }
}
