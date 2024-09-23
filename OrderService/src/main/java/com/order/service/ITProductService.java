package com.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.order.bean.TProduct;

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
    String addProduct(String productName);
    String addProductWithFeign(String productName);
    String addProductWithRemoteException(String productName);

}
