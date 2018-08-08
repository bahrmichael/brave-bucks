package com.bravebucks.eve.config.dbmigrations;

import com.bravebucks.eve.domain.SolarSystem;
import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;

import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Creates the initial database setup
 */
@ChangeLog(order = "002")
public class UpdateSolarSystems {

    @ChangeSet(order = "02", author = "rihan", id = "02-updateSolarSystems")
    public void addAuthorities(MongoTemplate mongoTemplate) {
        mongoTemplate.findAll(SolarSystem.class).forEach(s -> {
            s.setTrackPvp(true);
            s.setTrackRatting(false);
            mongoTemplate.save(s);
        });
    }
}
