package com.lsj.cloud.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ConfigController {

    @NacosValue(value = "${test.value}",autoRefreshed = true)
    private String textValue;

    @ResponseBody
    @GetMapping("/getTestValue")
    public String getTestValueConfig() {
        return textValue;
    }
}
