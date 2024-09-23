package com.order.service.impl;

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
        restTemplate.getForObject("http://stock-service/stock/reduceProduct?productId=" + productId + "&num=" + num, String.class);
        return "SUCCESS";
    }

    @Override
    @GlobalTransactional
    public String addProduct(String productName) {
        TProduct product = new TProduct();
        product.setProductName(productName);
        productMapper.insert(product);
        restTemplate.getForObject("http://stock-service/stock/addProductStock?productId=" + product.getId() + "&num=" + 100, String.class);
        return "SUCCESS";
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
}
