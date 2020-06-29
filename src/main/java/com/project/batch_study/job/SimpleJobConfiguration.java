package com.project.batch_study.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class SimpleJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job simpleJob() {
        return jobBuilderFactory.get("simpleJob")
                .start(simpleStepWithArgument(null))
                .next(exceptionStep(null))
                .next(simpleStep1())
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep1(){
        return stepBuilderFactory.get("simpleStep1")
                .tasklet((contribution, chunkContext) -> {
                    log.info(" >>>>>>>>> this is Step1 ");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    @JobScope
    public Step simpleStepWithArgument(@Value("#{jobParameters[requestData]}") String requestData){
        return stepBuilderFactory.get("simpleStepWithArgument")
                .tasklet((contribution, chunkContext) -> {
                    log.info(" >>>>>>>> request Data = {} ", requestData);
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    @JobScope
    public Step exceptionStep(@Value("#{jobParameters[requestData]}") String requestData){
        return stepBuilderFactory.get("exceptionStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info(" >>>>>>>>> exception step!!!!!!! ");
                    log.info(" exception 발생안함 {}", requestData);
                    //throw new IllegalAccessException("고의로 exception을 발생시킵니다.");
                    return RepeatStatus.FINISHED;
                }).build();
    }
}
