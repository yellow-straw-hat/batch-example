package io.pulse.config;

import io.pulse.property.EtlInputConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Input JPA configuration.
 */

@Configuration
public class InputJpaConfig {

    @Autowired
    private EtlInputConfigProperties etlInputConfigProperties;

    @Primary // because HibernateJpaConfiguration is @ConditionalOnSingleCandidate(DataSource.class)
    @Bean
    public DataSource dataSource() {

        EtlInputConfigProperties.EtlSource etlSource = etlInputConfigProperties.getSources().get(0);

        DataSourceProperties dsProperties = new DataSourceProperties();
        dsProperties.setUrl(etlSource.getUrl());
        dsProperties.setUsername(etlSource.getUsername());
        dsProperties.setPassword(etlSource.getPassword());

        // build the appropriate, DataSource
        return dsProperties.initializeDataSourceBuilder().build();
    }

}
