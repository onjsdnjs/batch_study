package com.project.batch_study.job;

import com.project.batch_study.domain.Pay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Slf4j
@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(name = "job.name", havingValue = PayCursorJobConfiguration.JOB_NAME)
public class PayCursorJobConfiguration {

    public static final String JOB_NAME = "payCursorJob";

    private final EntityManagerFactory entityManagerFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;
    private final DataSource dataSource;

    private final int chunkSize = 10;

    @Bean
    public Job payPagingJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(payPagingStep())
                .build();
    }

    @Bean
    @JobScope
    public Step payPagingStep() {
        return stepBuilderFactory.get("payPagingStep")
                .<Pay, Pay>chunk(chunkSize)
                .reader(payPagingReader())
                .processor(payPagingProcessor())
                .writer(writer())
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<Pay> payPagingReader() {
        return new JdbcCursorItemReaderBuilder<Pay>()
                .sql("SELECT * FROM pay p WHERE p.success_status = false")
                .rowMapper(new BeanPropertyRowMapper<>(Pay.class))
                .fetchSize(chunkSize)
                .dataSource(dataSource)
                .name("payPagingReader")
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Pay, Pay> payPagingProcessor() {
        return item -> {
            item.success();
            return item;
        };
    }

    @Bean
    @StepScope
    public JpaItemWriter<Pay> writer() {
        JpaItemWriter<Pay> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
}
