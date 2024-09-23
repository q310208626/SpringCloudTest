package com.order.controller;

import com.order.service.ITProductService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
    @GetMapping("/add")
    public String addProduct(@RequestParam("productName") String productName) {
        String response = tProductService.addProduct(productName);
        return response;
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
