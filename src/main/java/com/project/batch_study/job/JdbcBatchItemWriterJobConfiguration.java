package com.project.batch_study.job;

import com.project.batch_study.domain.Pay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

@Slf4j
@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(name = "job.name", havingValue = JdbcBatchItemWriterJobConfiguration.JOB_NAME)
public class JdbcBatchItemWriterJobConfiguration {
    public static final String JOB_NAME = "jdbcBatchItemWriterJob";
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource; // DataSource DI

    private static final int chunkSize = 10;

    @Bean
    public Job jdbcBatchItemWriterJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(jdbcBatchItemWriterStep())
                .build();
    }

    @Bean
    public Step jdbcBatchItemWriterStep() {
        return stepBuilderFactory.get("jdbcBatchItemWriterStep")
                .<Pay, Pay>chunk(chunkSize)
                .reader(jdbcBatchItemWriterReader())
                .writer(jdbcBatchItemWriter())
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Pay> jdbcBatchItemWriterReader() {
        return new JdbcCursorItemReaderBuilder<Pay>()
                .fetchSize(chunkSize)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Pay.class))
                .sql("SELECT id, amount, tx_name, tx_date_time FROM pay")
                .name("jdbcBatchItemWriter")
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Pay> jdbcBatchItemWriter() {
        JdbcBatchItemWriter<Pay> writer = new JdbcBatchItemWriterBuilder<Pay>()
                .dataSource(dataSource)
                .sql("insert into pay_duplicate(amount, tx_name, tx_date_time) values (:amount, :txName, :txDateTime)")
                .beanMapped()
                .build();

        writer.afterPropertiesSet(); // 각각의 Writer들이 실행되기 위해 필요한 필수값들이 제대로 세팅되어있는지를 체크한다.

        return writer;
    }

}
