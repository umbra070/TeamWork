import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class RecommendationsDataSource {
    Logger logger = LoggerFactory.getLogger(RecommendationsDataSource.class);
    @Bean(name = "recommendationsDataSource")
    public DataSource recommendationsDataSource(@Value("${application.recommendations-db.url}") String recommendationsDBUrl){
        try{
            DataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(recommendationsDBUrl);
            dataSource.setDriverClassName("org.h2.Driver");
            dataSource.setReadOnly(true);
            logger.info("Setting up Data Source with thous configuration:\n\r" +
                    "" + dataSource.getConnection().toString());
            return dataSource;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(e.toString());
        }

    }
}
