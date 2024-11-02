package com.user.service;

import com.user.bean.TUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author Soga
 * @since 2024-10-18
 */
public interface ITUserService extends IService<TUser> {
    public TUser loadUserByUsername(String username);
}
