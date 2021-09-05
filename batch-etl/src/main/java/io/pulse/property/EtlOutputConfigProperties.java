package io.pulse.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties of the output Datasource.
 *
 * @author yellowstrawhatter
 */
@Data
@ConfigurationProperties(prefix = "spring.jdbc.template")
public class EtlOutputConfigProperties {

    private int fetchSize;

}
