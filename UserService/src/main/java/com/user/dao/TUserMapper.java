package com.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.user.bean.TRole;
import com.user.bean.TUser;
import org.apache.ibatis.annotations.*;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author Soga
 * @since 2024-10-18
 */
public interface TUserMapper extends BaseMapper<TUser> {
    @Select("")
    @Results(id = "UserResultMap",
            value = {
                    @Result(property = "userId", column = "user_id"),
                    @Result(property = "userName", column = "user_name"),
                    @Result(property = "password", column = "password"),
                    @Result(property = "createTime", column = "create_time"),
                    @Result(property = "status", column = "status"),
                    @Result(property = "roles", column = "user_id", many = @Many(select = "selectRolesByUserId"))
            }
    )
    TUser resultMap();

    @Select({"select u.* from t_user u",
            "where user_name = #{userName}"})
    @ResultMap("com.user.dao.TUserMapper.UserResultMap")
    TUser selectByUserName(String userName);

    @Select({"select u.* from t_user u",
            "where user_id = #{userId}"})
    @ResultMap("com.user.dao.TUserMapper.UserResultMap")
    TUser selectById(Serializable id);

    @Select({"SELECT r.* FROM t_role r ",
            "RIGHT JOIN t_user_role ur ON r.role_id = ur.role_id",
            "WHERE ur.user_id = #{userId}"})
    @ResultMap("com.user.dao.TRoleMapper.TRoleResultMap")
    List<TRole> selectRolesByUserId(Long userId);
}
