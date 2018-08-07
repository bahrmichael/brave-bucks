package com.bravebucks.eve.repository;

import com.bravebucks.eve.domain.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for the User entity.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findOneByLogin(String login);

    Page<User> findAllByLoginNot(Pageable pageable, String login);

    Optional<User> findOneByCharacterId(Long characterId);

    List<User> findAllByWalletReadRefreshTokensNotNull();

    List<User> findAllByCharacterIdNotNull();
}
