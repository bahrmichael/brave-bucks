package com.buyback.eve.repository;

import com.buyback.eve.domain.Authority;
import com.buyback.eve.domain.Pool;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Pool entity.
 */
public interface PoolRepository extends MongoRepository<Pool, String> {
}
