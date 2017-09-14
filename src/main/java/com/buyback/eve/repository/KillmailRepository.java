package com.buyback.eve.repository;

import java.util.List;

import com.buyback.eve.domain.Killmail;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Killmail entity.
 */
public interface KillmailRepository extends MongoRepository<Killmail, String> {
    List<Killmail> findByCharacterId(long characterId);
}
