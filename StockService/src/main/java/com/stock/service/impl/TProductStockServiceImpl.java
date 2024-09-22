package com.stock.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stock.bean.TProductStock;
import com.stock.dao.TProductStockMapper;
import com.stock.service.ITProductStockService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 产品库存 服务实现类
 * </p>
 *
 * @author Soga
 * @since 2024-09-20
 */
@Service
public class TProductStockServiceImpl extends ServiceImpl<TProductStockMapper, TProductStock> implements ITProductStockService {

}
