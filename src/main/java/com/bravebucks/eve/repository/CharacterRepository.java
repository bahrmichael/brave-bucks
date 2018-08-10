package com.bravebucks.eve.repository;

import java.util.List;
import java.util.Optional;

import com.bravebucks.eve.domain.EveCharacter;
import com.bravebucks.eve.domain.User;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharacterRepository extends MongoRepository<EveCharacter, String> {
    List<EveCharacter> findByOwningUser(String owningUser);

    Optional<EveCharacter> findByNameAndOwningUser(final String characterName, final String id);

    List<EveCharacter> findByWalletReadRefreshTokenNotNull();
}
