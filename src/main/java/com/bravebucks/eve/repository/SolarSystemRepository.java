package com.bravebucks.eve.repository;

import java.util.List;

import com.bravebucks.eve.domain.SolarSystem;
import com.bravebucks.eve.domain.enumeration.Region;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the SolarSystem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SolarSystemRepository extends MongoRepository<SolarSystem, String> {
    List<SolarSystem> findByRegion(Region region);

    List<SolarSystem> findAllByTrackRatting(boolean trackRatting);

    List<SolarSystem> findAllByTrackPvp(boolean trackPvp);
}
