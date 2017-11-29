package com.bravebucks.eve.repository;

import java.util.List;

import com.bravebucks.eve.domain.SolarSystem;
import com.bravebucks.eve.domain.enumeration.Region;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the SolarSystem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SolarSystemRepository extends MongoRepository<SolarSystem, String> {
    List<SolarSystem> findByRegion(Region region);
}
