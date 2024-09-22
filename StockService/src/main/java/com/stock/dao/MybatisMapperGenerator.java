package com.stock.dao;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.nio.file.Paths;

public class MybatisMapperGenerator {

        public static void main(String[] args) {
            FastAutoGenerator.create("jdbc:mysql://localhost:3306/test_db?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai&remarks=true&useInformationSchema=true", "test_db", "test_db")
                    .globalConfig(builder -> builder
                            .author("Soga")
                            .outputDir(Paths.get(System.getProperty("user.dir")) + "/src/main/java")
                            .commentDate("yyyy-MM-dd")
                    )
                    .packageConfig(builder -> builder
                            .parent("com.stock")
                            .entity("bean")
                            .mapper("dao")
                            .service("service")
                            .serviceImpl("service.impl")
                            .xml("mapper.xml")
                    )
                    .strategyConfig(builder -> builder
                            .addInclude("t_product_stock")
                            .entityBuilder()
                            .enableLombok()
                            .enableFileOverride()
                    )
                    .templateEngine(new FreemarkerTemplateEngine())
                    .execute();
        }
}
