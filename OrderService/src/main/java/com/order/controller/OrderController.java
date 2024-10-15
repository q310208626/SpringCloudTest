package com.order.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.order.bean.TProduct;
import com.order.service.ITProductService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OrderController implements ApplicationContextAware {

    @Autowired
    ITProductService tProductService;

    @ResponseBody
    @GetMapping("/buy")
    public String orderProduct(@RequestParam("productId") String productId, @RequestParam("num") int num) {
        String response = tProductService.buyProduct(productId,num);
        return response;
    }

    @ResponseBody
    @GetMapping("/{productId}")
    @SentinelResource("query")
    public TProduct getProduct(@PathVariable("productId") String productId) {
        TProduct product = tProductService.getProduct(productId);
        return product;
    }

    @ResponseBody
    @GetMapping("/add")
    public TProduct addProduct(@RequestParam("productName") String productName) {
        String productId = tProductService.addProduct(productName);
        TProduct product = tProductService.getProduct(productId);
        return product;
    }

    @ResponseBody
    @GetMapping("/addWithException")
    public String addProductWithException(@RequestParam("productName") String productName) {
        String response = tProductService.addProductWithRemoteException(productName);
        return response;
    }

    @ResponseBody
    @GetMapping("/addWithFeign")
    public String addProductWithFeign(@RequestParam("productName") String productName) {
        String response = tProductService.addProductWithFeign(productName);
        return response;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println(applicationContext);
    }
}
