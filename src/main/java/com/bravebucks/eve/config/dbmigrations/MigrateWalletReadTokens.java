package com.bravebucks.eve.config.dbmigrations;

import com.bravebucks.eve.domain.EveCharacter;
import com.bravebucks.eve.domain.SolarSystem;
import com.bravebucks.eve.domain.User;
import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;

import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Creates the initial database setup
 */
@ChangeLog(order = "003")
public class MigrateWalletReadTokens {

    @ChangeSet(order = "01",
               author = "rihan",
               id = "01-migrateWalletReadTokens")
    public void addAuthorities(MongoTemplate mongoTemplate) {
        mongoTemplate.findAll(User.class).stream()
                     .filter(u -> u.getWalletReadRefreshTokens() != null)
                     .forEach(user -> {
                         user.getWalletReadRefreshTokens().forEach((characterId, refreshToken) -> {
                             EveCharacter character = new EveCharacter();
                             character.setId(characterId);
                             character.setOwningUser(user.getId());
                             character.setWalletReadRefreshToken(refreshToken);
                             mongoTemplate.save(character);
                         });
                         user.setWalletReadRefreshTokens(null);
                         mongoTemplate.save(user);
                     });
    }
}
