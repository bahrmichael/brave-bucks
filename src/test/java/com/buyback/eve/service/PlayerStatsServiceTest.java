package com.buyback.eve.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.buyback.eve.domain.Killmail;
import com.buyback.eve.domain.PlayerStats;
import com.buyback.eve.domain.Pool;
import com.buyback.eve.domain.User;
import com.buyback.eve.repository.KillmailRepository;
import com.buyback.eve.repository.PayoutRepository;
import com.buyback.eve.repository.PoolRepository;
import com.buyback.eve.repository.TransactionRepository;
import com.buyback.eve.repository.UserRepository;
import static com.buyback.eve.service.DateUtil.getYearMonth;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.flapdoodle.embed.process.collections.Collections;

public class PlayerStatsServiceTest {

    private static final long COINS = 7L;
    private static final long POOL = 100_000_000L;
    private UserRepository userRepositoryMock = mock(UserRepository.class);
    private KillmailRepository killmailRepository = mock(KillmailRepository.class);
    private PoolRepository poolRepository = mock(PoolRepository.class);
    private TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private PayoutRepository payoutRepository = mock(PayoutRepository.class);
    private PlayerStatsService sut = new PlayerStatsService(userRepositoryMock, killmailRepository, poolRepository,
                                                            transactionRepository, payoutRepository);

    @Test
    public void getStatsForUser() throws Exception {
        String username = "someUser";
        long characterId = 1L;

        when(userRepositoryMock.findOneByLogin(username)).thenReturn(createUser(characterId));
        when(killmailRepository.findByAttackerId(characterId)).thenReturn(createKillList(characterId));
        when(poolRepository.findByYearMonth(getYearMonth(LocalDate.now()))).thenReturn(createPool(COINS));

        PlayerStats playerStats = sut.getStatsForUser(username);

        assertEquals(COINS, playerStats.getCoins());
        assertEquals(5L, playerStats.getDefenseKills());
        assertEquals(1L, playerStats.getFinalBlows());
        assertEquals(POOL, playerStats.getPotentialPayout());
    }

    @Test
    public void getStatsForUser_with0ClaimedCoins_thenPayoutIs0() throws Exception {
        String username = "someUser";
        long characterId = 1L;

        when(userRepositoryMock.findOneByLogin(username)).thenReturn(createUser(characterId));
        when(killmailRepository.findByAttackerId(characterId)).thenReturn(createKillList(characterId));
        when(poolRepository.findByYearMonth(getYearMonth(LocalDate.now()))).thenReturn(createPool(0L));

        PlayerStats playerStats = sut.getStatsForUser(username);

        assertEquals(COINS, playerStats.getCoins());
        assertEquals(5L, playerStats.getDefenseKills());
        assertEquals(1L, playerStats.getFinalBlows());
        assertEquals(0L, playerStats.getPotentialPayout());
    }

    private Optional<Pool> createPool(final Long coins) {
        final Pool pool = new Pool();
        pool.setYearMonth(getYearMonth(LocalDate.now()));
        pool.setBalance(POOL);
        pool.setClaimedCoins(coins);
        return Optional.of(pool);
    }

    private List<Killmail> createKillList(final long characterId) {
        String date = getYearMonth(LocalDate.now()) + "-02T17:50:11Z";
        return Collections.newArrayList(
            new Killmail("2017-03-02T17:50:11Z", 5),
            new Killmail(date, 2),
            new Killmail(date, 3),
            new Killmail(date, 1),
            new Killmail(date, 2),
            new Killmail(date, 2, characterId)
                                       );
    }

    private Optional<User> createUser(long characterId) {
        final User user = new User();
        user.setCharacterId(characterId);
        return Optional.of(user);
    }

    @Test
    public void getStatsForCurrentUser_callsGetStatsForUser() throws Exception {
        PlayerStatsService sut = spy(new PlayerStatsService(null, null, null, transactionRepository, payoutRepository));
        doReturn(null).when(sut).getStatsForUser(anyString());

        sut.getStatsForCurrentUser();

        verify(sut).getStatsForUser(anyString());
    }

    @Test
    public void getStatusForUser_withoutUserResult_returnsNull() throws Exception {
        when(userRepositoryMock.findOneByLogin(anyString())).thenReturn(Optional.empty());

        PlayerStats statsForUser = sut.getStatsForUser("");

        assertNull(statsForUser);
    }

    @Test
    public void getStatusForUser_withoutPoolResult_returnsNull() throws Exception {
        when(userRepositoryMock.findOneByLogin(anyString())).thenReturn(Optional.of(new User()));
        when(poolRepository.findByYearMonth(anyString())).thenReturn(Optional.empty());

        PlayerStats statsForUser = sut.getStatsForUser("");

        assertNull(statsForUser);

    }
}
