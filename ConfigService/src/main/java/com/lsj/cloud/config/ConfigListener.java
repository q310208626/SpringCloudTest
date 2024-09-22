package com.lsj.cloud.config;

import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class ConfigListener {

    @NacosConfigListener(dataId = "config.service")
    public void configListener(String config) {
        try {
            Properties configServiceProperties = new Properties();
            configServiceProperties.load(new ByteArrayInputStream(config.getBytes()));

            System.out.println("config change :" + configServiceProperties.getProperty("test.value"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
