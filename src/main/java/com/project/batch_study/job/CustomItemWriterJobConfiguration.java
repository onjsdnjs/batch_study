package com.project.batch_study.job;

import com.project.batch_study.domain.Pay;
import com.project.batch_study.domain.PayDuplicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(name = "job.name", havingValue = CustomItemWriterJobConfiguration.JOB_NAME)
public class CustomItemWriterJobConfiguration {
    public static final String JOB_NAME = "customItemWriterJob";
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private static final int chunkSize = 10;

    @Bean
    public Job jpaItemWriterJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(jpaItemWriterStep())
                .build();
    }

    @Bean
    public Step jpaItemWriterStep() {
        return stepBuilderFactory.get("jpaItemWriterStep")
                .<Pay, PayDuplicate>chunk(chunkSize)
                .reader(jpaItemWriterReader())
                .processor(jpaItemProcessor())
                .writer(customItemWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Pay> jpaItemWriterReader() {
        return new JpaPagingItemReaderBuilder<Pay>()
                .name("jpaItemWriterReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT p FROM Pay p")
                .build();
    }

    @Bean
    public ItemProcessor<Pay, PayDuplicate> jpaItemProcessor(){
        return pay -> PayDuplicate.builder()
                .txName(pay.getTxName())
                .amount(pay.getAmount())
                .DateTime(pay.getTxDateTime())
                .successStatus(pay.isSuccessStatus())
                .build();
    }

    /*@Bean
    public ItemWriter<PayDuplicate> customItemWriter() {
        return new ItemWriter<PayDuplicate>() {
            @Override
            public void write(List<? extends PayDuplicate> items) throws Exception {
                for (PayDuplicate payDuplicate : items){
                    System.out.println(payDuplicate);
                }
            }
        };
    }*/

    @Bean
    public ItemWriter<PayDuplicate> customItemWriter() {
        return items -> {
            for (PayDuplicate payDuplicate : items){
                System.out.println(payDuplicate);
            }
        };
    }

}
