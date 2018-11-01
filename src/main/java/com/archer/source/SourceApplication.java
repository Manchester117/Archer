package com.archer.source;

import com.github.pagehelper.PageHelper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

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

    /** 
    * @description: 解决跨域的问题 
    * @author:      Zhao.Peng 
    * @date:        2018/11/1 
    * @time:        15:39 
    * @param:        
    * @return:      consConfiguration - 跨域配置
    */
    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        return corsConfiguration;
    }

    /**
     * 跨域过滤器
     * @return
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig()); // 4
        return new CorsFilter(source);
    }

    public static void main(String[] args) {
        SpringApplication.run(SourceApplication.class, args);
    }
}
