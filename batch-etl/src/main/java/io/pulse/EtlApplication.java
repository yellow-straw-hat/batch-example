package io.pulse;


import io.pulse.property.EtlInputConfigProperties;
import io.pulse.property.EtlOutputConfigProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static org.springframework.boot.Banner.Mode.LOG;
import static org.springframework.boot.WebApplicationType.NONE;

/**
 * The main app execution entry point.
 * <p>
 * Excluding persistence autoconfigs to define 2 JPA persistence units (input/output).
 *
 * @author yellowstrawhatter
 * @see io.pulse.config.InputJpaConfig
 * @see io.pulse.config.OutputJdbcConfig
 */
@EnableJpaRepositories
@EnableConfigurationProperties({EtlInputConfigProperties.class, EtlOutputConfigProperties.class})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class EtlApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(EtlApplication.class)
                .bannerMode(LOG)
                .web(NONE)
                .run(args);
    }
}
