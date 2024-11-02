package com.service.user.dao;

import com.user.UserServiceApplication;
import com.user.bean.TUser;
import com.user.dao.TUserMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = UserServiceApplication.class)
public class UserMapperTest {

    @Autowired
    private TUserMapper userMapper;

    @Test
    public void queryUserWithRoles(){
        TUser user = userMapper.selectByUserName("test");
        Assert.assertNotNull(user);
    }


}
