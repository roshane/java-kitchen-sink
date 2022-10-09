package com.aeon.restrictionpoc.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class MybatisConfig {

    @Value("${mapper-locations}")
    @NonNull
    private List<String> mapperLocations;

    private Resource[] mapperFiles() {
        return mapperLocations
                .stream()
                .map(ClassPathResource::new)
                .toArray(Resource[]::new);
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(mapperFiles());
        return factoryBean.getObject();
    }
}
