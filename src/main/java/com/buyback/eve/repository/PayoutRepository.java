package com.buyback.eve.repository;

import com.buyback.eve.domain.Payout;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Payout entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PayoutRepository extends MongoRepository<Payout, String> {

}
