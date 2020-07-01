package com.project.batch_study.job;

import com.project.batch_study.domain.Pay;
import com.project.batch_study.domain.PayRepository;
import com.project.batch_study.utils.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@SpringBootTest
@SpringBatchTest
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {"job.name = " +PayPagingFailJobConfiguration.JOB_NAME})
class PayPagingFailJobConfigurationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private PayRepository payRepository;

    @Test
    @DisplayName("같은 조건을 읽고 업데이트 할 때")
    void test1() throws Exception {
        List<Pay> pays = new ArrayList<>();

        for (int i = 0; i < 30; ++i) {
            Pay newData = Pay.builder()
                    .txName(TestUtil.getRandomString(10))
                    .DateTime(LocalDateTime.now())
                    .amount(TestUtil.getRandomLong(1000, 100000))
                    .successStatus(false)
                    .build();
            pays.add(newData);
        }
        payRepository.saveAll(pays);
        List<String> savedPays = pays.stream().map(pay -> pay.getTxName()).collect(Collectors.toList());

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        List<Pay> updatedPays = payRepository.findAll().stream().filter(pay -> savedPays.contains(pay.getTxName())).collect(Collectors.toList());
        assertThat(jobExecution.getStatus(), equalTo(BatchStatus.COMPLETED));
        // 아래 테스트를 통과하지 못한다.
        assertThat(updatedPays.stream().filter(pay -> pay.isSuccessStatus() == false).collect(Collectors.toList()).size(), equalTo(0));
    }
}
