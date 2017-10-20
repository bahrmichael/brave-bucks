package com.buyback.eve.repository;

import com.buyback.eve.domain.SolarSystem;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the SolarSystem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SolarSystemRepository extends MongoRepository<SolarSystem, String> {

}
