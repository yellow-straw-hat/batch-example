package io.pulse.config;

import io.pulse.property.EtlOutputConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Output JDBC configuration.
 */
@EnableConfigurationProperties(DataSourceProperties.class)
@Configuration
public class OutputJdbcConfig {

    @Autowired
    private DataSourceProperties outputDataSourceProperties;

    @Autowired
    private EtlOutputConfigProperties etlOutputConfigProperties;

    @FlywayDataSource
    @Bean
    public DataSource outputDataSource() {
        return outputDataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    public JdbcTemplate outputJdbcTemplate(@Qualifier("outputDataSource") DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setFetchSize(etlOutputConfigProperties.getFetchSize());
        return jdbcTemplate;
    }

}
