package com.project.batch_study.domain;

import com.project.batch_study.utils.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class PayRepositoryTest {

    @Autowired
    private PayRepository payRepository;

    @Test
    void insertTest(){
        Pay newData = Pay.builder()
                .txName(TestUtil.getRandomString(10))
                .DateTime(LocalDateTime.now())
                .amount(TestUtil.getRandomLong(1000, 100000))
                .build();

        payRepository.save(newData);

        assertThat(Optional.ofNullable(newData.getId()).isPresent(), equalTo(true));
    }

}
