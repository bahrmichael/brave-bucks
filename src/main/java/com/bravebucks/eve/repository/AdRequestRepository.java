package com.bravebucks.eve.repository;

import java.util.List;
import java.util.Optional;

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

    List<AdRequest> findByAdStatus(AdStatus status);

    List<AdRequest> findByMonth(String month);

    Optional<AdRequest> findByServiceAndMonth(String service, String month);

    int countByAdStatus(AdStatus status);
}
