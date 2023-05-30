package com.credibank.cards.repository;

import com.credibank.cards.domain.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    boolean existsById(Long id);

    @Modifying
    @Query("UPDATE Transaction t  SET t.cancel = true WHERE t.id = :transactionId")
    void cancel(Long transactionId);

}
