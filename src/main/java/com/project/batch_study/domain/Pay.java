package com.project.batch_study.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ToString
@Data
@NoArgsConstructor
@Entity
@DynamicUpdate
public class Pay {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long amount;
    private String txName;
    private LocalDateTime txDateTime;
    private boolean successStatus;

    public Pay(Long amount, String txName, String txDateTime){
        this.amount = amount;
        this.txName = txName;
        this.txDateTime = LocalDateTime.parse(txDateTime, FORMATTER);
    }

    @Builder
    public Pay(Long amount, String txName, LocalDateTime DateTime, boolean successStatus){
        this.amount = amount;
        this.txName = txName;
        this.txDateTime = DateTime;
        this.successStatus = successStatus;
    }

    public Pay(Long id, Long amount, String txName, String txDateTime) {
        this.id = id;
        this.amount = amount;
        this.txName = txName;
        this.txDateTime = LocalDateTime.parse(txDateTime, FORMATTER);
    }

    public void success() {
        this.successStatus = true;
    }
}
