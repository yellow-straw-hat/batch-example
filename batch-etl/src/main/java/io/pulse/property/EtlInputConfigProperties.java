package io.pulse.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Properties of the input Datasource.
 *
 * @author yellowstrawhatter
 */
@Data
@ConfigurationProperties(prefix = "etl.input")
public class EtlInputConfigProperties {

    private List<String> ctWhitelist;
    private List<EtlSource> sources;
    private int readPageSize;

    @PostConstruct
    public void afterPropertiesSet() {
        Assert.notEmpty(sources, "At least 1 source must be configured");
    }

    @Data
    public static class EtlSource {
        private String name;
        private String url;
        private String username;
        private String password;
    }

}
