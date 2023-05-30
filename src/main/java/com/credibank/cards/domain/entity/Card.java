package com.credibank.cards.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "card")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 6, max = 6)
    @Column(name = "product_id", length = 6, nullable = false)
    private String productId;

    @Size(min = 16, max = 16)
    @Column(name = "card_number", length = 16, nullable = false)
    private String cardNumber;

    @Column(name = "card_holder")
    private String cardHolder;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "enroll")
    private boolean enroll;

    @Column(name = "block")
    private boolean block;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "valid_thru", length = 7, nullable = false)
    private String validThru;
}

