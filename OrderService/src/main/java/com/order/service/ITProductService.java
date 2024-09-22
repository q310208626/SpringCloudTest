package com.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.order.bean.TProduct;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * 产品 服务类
 * </p>
 *
 * @author Soga
 * @since 2024-09-20
 */
public interface ITProductService extends IService<TProduct> {
    String buyProduct(String productId, int num);
    String addProduct(@RequestParam("productName") String productName);
    String addProductWithRemoteException(@RequestParam("productName") String productName);

}
