package com.bravebucks.eve.web.rest;

import java.util.Set;
import java.util.stream.Collectors;

import com.bravebucks.eve.domain.EveCharacter;
import com.bravebucks.eve.domain.User;
import com.bravebucks.eve.repository.CharacterRepository;
import com.bravebucks.eve.repository.UserRepository;
import com.bravebucks.eve.security.SecurityUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RattingResource {

    private static final Logger log = LoggerFactory.getLogger(RattingResource.class);

    private final UserRepository userRepository;
    private final CharacterRepository characterRepository;

    public RattingResource(final UserRepository userRepository,
                           final CharacterRepository characterRepository) {
        this.userRepository = userRepository;
        this.characterRepository = characterRepository;
    }

    @GetMapping("/characters")
    public Set<String> getCharacters() {
        final User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        return characterRepository.findByOwningUser(user.getId()).stream()
                                  .filter(c -> c.getWalletReadRefreshToken() != null)
                                  .map(EveCharacter::getName).collect(Collectors.toSet());
    }

    @DeleteMapping("/characters/{characterName}")
    public void deleteUser(@PathVariable("characterName") final String characterName) {
        log.info("{} revokes {}", SecurityUtils.getCurrentUserLogin(), characterName);
        userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(user -> {
            characterRepository.findByNameAndOwningUser(characterName, user.getId())
                               .ifPresent(c -> {
                                   c.setWalletReadRefreshToken(null);
                                   characterRepository.save(c);
                               });
        });
    }
}
