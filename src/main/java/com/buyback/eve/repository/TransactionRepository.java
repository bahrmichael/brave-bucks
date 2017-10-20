package com.buyback.eve.repository;

import java.util.List;

import com.buyback.eve.domain.Transaction;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Transaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findAllByUser(String user);
}
