package org.skypro.teamwork.configurations;

import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Primary
    @Bean(name="defaultDataSource")
    public DataSource defaultDataSource(DataSourceProperties properties){
        return properties.initializeDataSourceBuilder().build();
    }
}
