package com.project.batch_study.job;

import com.project.batch_study.domain.Pay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(name = "job.name", havingValue = JdbcPagingItemReaderJobConfiguration.JOB_NAME)
public class JdbcPagingItemReaderJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource; // DataSource DI

    private static final int CHUNKSIZE = 10;

    public static final String JOB_NAME = "jdbcPagingItemReaderJob";

    @Bean
    public Job jdbcPagingItemReaderJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(jdbcPagingItemReaderStep())
                .build();
    }

    @Bean
    public Step jdbcPagingItemReaderStep() {
        return stepBuilderFactory.get("jdbcPagingItemReaderStep")
                .<Pay, Pay>chunk(CHUNKSIZE)
                .reader(jdbcPagingItemReader())
                .writer(jdbcPagingItemWriter())
                .build();
    }

    @Bean
    public JdbcPagingItemReader<Pay> jdbcPagingItemReader() {
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("amount", 50000);

        return new JdbcPagingItemReaderBuilder<Pay>()
                .pageSize(CHUNKSIZE)
                .fetchSize(CHUNKSIZE)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Pay.class))
                .queryProvider(createQueryProvider())
                .parameterValues(parameterValues)
                .name("jdbcPagingItemReader")
                .build();
    }

    private ItemWriter<Pay> jdbcPagingItemWriter() {
        return list -> {
            for (Pay pay : list) {
                log.info("Current Pay={}", pay);
            }
        };
    }

    @Bean
    public PagingQueryProvider createQueryProvider() {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();

        try {
            queryProvider.setDataSource(dataSource);
            queryProvider.setSelectClause("id, amount, tx_name, tx_date_time");
            queryProvider.setFromClause("from pay");
            queryProvider.setWhereClause("where amount >= :amount");

            //Map<String, Order> sortKeys = new HashMap<>(1);
            //sortKeys.put("id", Order.ASCENDING);

            queryProvider.setSortKey("id");

            return queryProvider.getObject();
        } catch (Exception e) {
            e.getStackTrace();
        }

        return null;

    }

}
