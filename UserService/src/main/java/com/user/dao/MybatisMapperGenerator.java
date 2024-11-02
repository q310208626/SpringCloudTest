package com.user.dao;

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
                            .parent("com.user")
                            .entity("bean")
                            .mapper("dao")
                            .service("service")
                            .serviceImpl("service.impl")
                            .xml("mapper.xml")
                    )
                    .strategyConfig(builder -> builder
                            .addInclude("t_user","t_role","t_user_role")
                            .entityBuilder()
                            .enableLombok()
                            .enableFileOverride()
                    )
                    .templateEngine(new FreemarkerTemplateEngine())
                    .execute();
        }
}
