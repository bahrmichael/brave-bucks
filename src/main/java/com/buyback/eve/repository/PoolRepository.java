package com.buyback.eve.repository;

import java.util.Optional;

import com.buyback.eve.domain.Pool;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Pool entity.
 */
public interface PoolRepository extends MongoRepository<Pool, String> {
    Optional<Pool> findByYearMonth(String yearMonth);
}
