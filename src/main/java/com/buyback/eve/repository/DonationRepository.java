package com.buyback.eve.repository;

import com.buyback.eve.domain.Donation;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Donation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DonationRepository extends MongoRepository<Donation, String> {

}
