package com.bravebucks.eve.repository;

import java.util.List;
import java.util.Optional;

import com.bravebucks.eve.domain.Payout;
import com.bravebucks.eve.domain.enumeration.PayoutStatus;

import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Payout entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PayoutRepository extends MongoRepository<Payout, String> {
    Optional<Payout> findOneByUser(String user);

    List<Payout> findAllByUserAndStatus(String user, PayoutStatus status);

    int countByStatus(PayoutStatus status);
}
