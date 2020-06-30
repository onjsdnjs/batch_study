package com.project.batch_study.domain;

import com.project.batch_study.utils.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class PayRepositoryTest {

    @Autowired
    private PayRepository payRepository;

    @Test
    void insertTest() {
        Pay newData = Pay.builder()
                .txName(TestUtil.getRandomString(10))
                .DateTime(LocalDateTime.now())
                .amount(TestUtil.getRandomLong(1000, 100000))
                .successStatus(false)
                .build();

        payRepository.save(newData);

        assertThat(Optional.ofNullable(newData.getId()).isPresent(), equalTo(true));
    }

    @Test
    @DisplayName("배치에 사용할 데이터 추가용. 테스트 코드 아님")
    void insertData() {
        List<Pay> pays = new ArrayList<>();

        for (int i = 0; i < 100; ++i) {
            boolean successStatus = true;
            if (i % 3 == 0) {
                successStatus = false;
            }

            Pay newData = Pay.builder()
                    .txName(TestUtil.getRandomString(10))
                    .DateTime(LocalDateTime.now())
                    .amount(TestUtil.getRandomLong(1000, 100000))
                    .successStatus(successStatus)
                    .build();

            pays.add(newData);
        }

        payRepository.saveAll(pays);
    }

}
