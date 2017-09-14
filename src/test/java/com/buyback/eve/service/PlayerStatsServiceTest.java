package com.buyback.eve.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.buyback.eve.domain.Killmail;
import com.buyback.eve.domain.PlayerStats;
import com.buyback.eve.domain.Pool;
import com.buyback.eve.domain.User;
import com.buyback.eve.repository.KillmailRepository;
import com.buyback.eve.repository.PoolRepository;
import com.buyback.eve.repository.UserRepository;

import static com.buyback.eve.service.KillmailToPoolTransformer.getYearMonth;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.flapdoodle.embed.process.collections.Collections;

public class PlayerStatsServiceTest {

    private UserRepository userRepositoryMock = mock(UserRepository.class);
    private KillmailRepository killmailRepository = mock(KillmailRepository.class);
    private PoolRepository poolRepository = mock(PoolRepository.class);
    private PlayerStatsService sut = new PlayerStatsService(userRepositoryMock, killmailRepository, poolRepository);

    @Test
    public void getStatsForUser() throws Exception {
        String username = "someUser";
        long characterId = 1L;

        when(userRepositoryMock.findOneByLogin(username)).thenReturn(createUser(characterId));
        when(killmailRepository.findByCharacterId(characterId)).thenReturn(createKillList(characterId));
        when(poolRepository.findByYearMonth(getYearMonth(LocalDate.now()))).thenReturn(createPool());

        PlayerStats playerStats = sut.getStatsForUser(username);

        assertEquals(10L, playerStats.getCoins());
        assertEquals(5L, playerStats.getDefenseKills());
        assertEquals(1L, playerStats.getFinalBlows());
        assertEquals(100_000_000L, playerStats.getPotentialPayout());
    }

    private Optional<Pool> createPool() {
        final Pool pool = new Pool();
        pool.setYearMonth(getYearMonth(LocalDate.now()));
        pool.setBalance(100_000_000L);
        return Optional.of(pool);
    }

    private List<Killmail> createKillList(final long characterId) {
        String date = getYearMonth(LocalDate.now()) + "-02 17:50:11";
        return Collections.newArrayList(
            new Killmail(characterId, "2017-03-02 17:50:11", 5),
            new Killmail(characterId, date, 2),
            new Killmail(characterId, date, 3),
            new Killmail(characterId, date, 1),
            new Killmail(characterId, date, 2),
            new Killmail(characterId, date, 2, true)
                                       );
    }

    private Optional<User> createUser(long characterId) {
        final User user = new User();
        user.setCharacterId(characterId);
        return Optional.of(user);
    }
}
