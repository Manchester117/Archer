package com.archer.source;

import com.github.pagehelper.PageHelper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

@SpringBootApplication
@MapperScan("com.archer.source.mapper")                            // 扫描mapper
public class SourceApplication {
    /** 
    * @description: 使用PageHelper进行分页 
    * @author:      Zhao.Peng 
    * @date:        2018/8/27 
    * @time:        17:33 
    * @param:        
    * @return:      PageHelper配置实例
    */
    @Bean
    public PageHelper pageHelper() {
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum", "true");
        properties.setProperty("rowBoundsWithCount", "true");
        properties.setProperty("reasonable", "true");
        properties.setProperty("dialect", "mysql");                 // 使用MySQL的方言
        pageHelper.setProperties(properties);

        return pageHelper;
    }

    public static void main(String[] args) {
        SpringApplication.run(SourceApplication.class, args);
    }
}
