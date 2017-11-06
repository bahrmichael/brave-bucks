package com.bravebucks.eve.repository;

import com.bravebucks.eve.domain.AdRequest;
import com.bravebucks.eve.domain.enumeration.AdStatus;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the AdRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AdRequestRepository extends MongoRepository<AdRequest, String> {

    AdRequest findByAdStatus(AdStatus status);
}
