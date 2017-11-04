package com.bravebucks.eve.repository;

import com.bravebucks.eve.domain.SolarSystem;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the SolarSystem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SolarSystemRepository extends MongoRepository<SolarSystem, String> {

}
