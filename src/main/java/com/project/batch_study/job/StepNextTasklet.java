package com.project.batch_study.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@StepScope
public class StepNextTasklet implements Tasklet {

    @Value("#{jobParameters[requestData]}")
    private String requestData;

    public StepNextTasklet(){
        log.info(" >>>>>>>>>>>>> Task 생성  ");
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info(" >>>>>>>>>>>>> StepNextTasklet ");
        log.info(" >>>>>>>>>>>>> requestData : {}",requestData);
        return RepeatStatus.FINISHED;
    }
}
