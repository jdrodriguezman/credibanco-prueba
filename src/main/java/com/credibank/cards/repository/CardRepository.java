package com.credibank.cards.repository;

import com.credibank.cards.domain.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardNumber(String cardNumber);

    boolean existsByCardNumber(String cardNumber);

    @Modifying
    @Query("UPDATE Card c  SET c.enroll = true WHERE c.cardNumber = :cardNumber")
    void enrollCard(String cardNumber);

    @Modifying
    @Query("UPDATE Card c  SET c.block = true WHERE c.cardNumber = :cardNumber")
    void blockCard(String cardNumber);

    @Modifying
    @Query("UPDATE Card c  SET c.balance = c.balance + :balance WHERE c.cardNumber = :cardNumber")
    void rechargeBalance(String cardNumber, BigDecimal balance);

    @Query("SELECT c.balance FROM Card c WHERE c.cardNumber = :cardNumber")
    double getBalance(String cardNumber);

    @Modifying
    @Query("UPDATE Card c  SET c.balance = c.balance - :price WHERE c.cardNumber = :cardNumber")
    void deductBalance(String cardNumber, BigDecimal price);
}

