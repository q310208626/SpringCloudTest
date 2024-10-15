package com.order.service.impl;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.order.bean.TProduct;
import com.order.dao.TProductMapper;
import com.order.service.IRemoteStockService;
import com.order.service.ITProductService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * <p>
 * 产品 服务实现类
 * </p>
 *
 * @author Soga
 * @since 2024-09-20
 */
@Service
public class TProductServiceImpl extends ServiceImpl<TProductMapper, TProduct> implements ITProductService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private TProductMapper productMapper;

    @Autowired
    IRemoteStockService remoteStockService;


    @Override
    public String buyProduct(String productId, int num) {
        remoteStockService.reduceProduct(productId,num);
        return "SUCCESS";
    }

    @Override
    @GlobalTransactional
    public String addProduct(String productName) {
        TProduct product = new TProduct();
        product.setProductName(productName);
        productMapper.insert(product);
        restTemplate.getForObject("http://stock-service/stock/addProductStock?productId=" + product.getId() + "&num=" + 100, String.class);
        return String.valueOf(product.getId());
    }

    @Override
    public String addProductWithFeign(String productName) {
        TProduct product = new TProduct();
        product.setProductName(productName);
        productMapper.insert(product);
        remoteStockService.addProductStock(product.getId(),101);
        return "SUCCESS";
    }

    @Override
    @GlobalTransactional
    public String addProductWithRemoteException(String productName) {
        TProduct product = new TProduct();
        product.setProductName(productName);
        productMapper.insert(product);
        restTemplate.getForObject("http://stock-service/stock/addProductStockWithException?productId=" + product.getId() + "&num=" + 100, String.class);
        return "SUCCESS";
    }

    @Override
//    @SentinelResource(value = "order", blockHandler = "blockHandler")
    public TProduct getProduct(String productId) {
        return productMapper.selectById(productId);
    }

    public String blockHandler(String productId, BlockException ex) {
        System.out.println("访问过于频繁，请稍后再试");
        return "访问过于频繁，请稍后再试";
    }
}
