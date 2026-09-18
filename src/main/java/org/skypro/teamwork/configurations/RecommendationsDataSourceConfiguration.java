package org.skypro.teamwork.configurations;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class RecommendationsDataSourceConfiguration {
    Logger logger = LoggerFactory.getLogger(RecommendationsDataSourceConfiguration.class);
    @Bean(name = "recommendationsDataSource")
    public DataSource recommendationsDataSource(@Value("${application.recommendations-db.url}") String recommendationsDBUrl){
        try{
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(recommendationsDBUrl);
            dataSource.setDriverClassName("org.h2.Driver");
            dataSource.setReadOnly(true);
            logger.info("Setting up Data Source with thous configuration:\n\r" +
                    "" + recommendationsDBUrl);
            return dataSource;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Bean(name = "recommendationsJdbcTemplate")
    public JdbcTemplate recommendationsJdbcTemplate(@Qualifier("recommendationsDataSource") DataSource dataSource){
        try{
            return new JdbcTemplate(dataSource);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
