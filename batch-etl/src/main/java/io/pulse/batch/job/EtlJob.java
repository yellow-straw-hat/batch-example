package io.pulse.batch.job;

import io.pulse.batch.reader.PagingCustomerItemReader;
import io.pulse.domain.entity.SourceCustomer;
import io.pulse.entity.Customer;
import io.pulse.property.EtlInputConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static java.util.stream.Collectors.toList;
import static org.springframework.batch.repeat.RepeatStatus.FINISHED;

/**
 * All batch jobs are setup up in this class.
 *
 * @author yellowstrawhatter
 */
@EnableBatchProcessing
@Configuration
@Slf4j
public class EtlJob {

    /**
     * SQL query to "upsert" customers into the output DB.
     */
    private static final String SQL_UPSERT = "INSERT INTO ...";

    /**
     * Convenient factory for a {@link org.springframework.batch.core.step.builder.StepBuilder} which sets the {@link org.springframework.batch.core.repository.JobRepository} and {@link org.springframework.transaction.PlatformTransactionManager} automatically..
     */
    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private EtlInputConfigProperties etlInputConfigProperties;


    /**
     * The output datasource where to persist data.
     */
    @Autowired
    @Qualifier("outputDataSource")
    private DataSource outputDataSource;

    /**
     * The configured {@link JdbcTemplate}.
     */
    @Autowired
    @Qualifier("outputJdbcTemplate")
    private JdbcTemplate outputJdbcTemplate;

    // We suppose we have multiple sources
    private Step initStep() {
        return steps.get("step-init")
                .tasklet((contribution, chunkContext) -> {
                    log.info("Processing {} sources: {}",
                            etlInputConfigProperties.getSources().size(),
                            etlInputConfigProperties.getSources().stream().map(EtlInputConfigProperties.EtlSource::getName).collect(toList()));
                    log.info("CT white list: {}", etlInputConfigProperties.getCtWhitelist());
                    return FINISHED;
                }).build();
    }

    private Step stepFor(EtlInputConfigProperties.EtlSource etlSource) {
        return steps.get(etlSource.getName())
                .<SourceCustomer, Customer>chunk(etlInputConfigProperties.getReadPageSize())
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }


    @Bean
    public ItemReader<SourceCustomer> itemReader() {
        return new PagingCustomerItemReader(etlInputConfigProperties.getReadPageSize());
    }

    /**
     * @return A processed customer
     */
    @Bean
    public ItemProcessor<SourceCustomer, Customer> itemProcessor() {
        // Fitering, transformation, etc..
        return item ->
                Customer.builder()
                        .fullname(item.getFirstname() + "" + item.getLastname())
                        .build();
    }

    @Bean
    public ItemWriter<Customer> itemWriter() {
        JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>() {
            @Override
            public void afterPropertiesSet() {
                // don't fail with pg-specific casting syntax
            }
        };
        itemWriter.setDataSource(outputDataSource);
        itemWriter.setSql(SQL_UPSERT);
        outputJdbcTemplate.batchUpdate("...");
        return itemWriter;
    }

    @Bean
    public Job etlJob(JobBuilderFactory jobs) {
        SimpleJobBuilder simpleJobBuilder = jobs.get("customer-etl").start(initStep());
        etlInputConfigProperties.getSources().stream()
                .map(this::stepFor)
                .forEach(simpleJobBuilder::next);
        return simpleJobBuilder.build();
    }
}
