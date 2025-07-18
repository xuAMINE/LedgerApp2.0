package com.example.bankapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal amount;
    private String type;
    private LocalDateTime timestamp;

    private String description;

    @Column(name = "counterparty")
    private String counterparty;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

}
