package com.buyback.eve.web.rest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.buyback.eve.domain.Pool;
import com.buyback.eve.repository.PoolRepository;

import static com.buyback.eve.service.DateUtil.getYearMonth;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PoolsResourceTest {

    private PoolRepository repo = mock(PoolRepository.class);
    private PoolsResource sut = new PoolsResource(repo);

    @Test
    public void getPlayerStats() throws Exception {
        when(repo.findByYearMonth(getYearMonth(LocalDate.now().minusMonths(1)))).thenReturn(Optional.of(
            createPoolWithBalance(1L)));
        when(repo.findByYearMonth(getYearMonth(LocalDate.now()))).thenReturn(Optional.of(
            createPoolWithBalance(3L)));
        when(repo.findByYearMonth(getYearMonth(LocalDate.now().plusMonths(1)))).thenReturn(Optional.of(
            createPoolWithBalance(7L)));

        ResponseEntity responseEntity = sut.getPlayerStats();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<Long> result = (List<Long>) responseEntity.getBody();
        assertEquals(7L, result.get(0).longValue());
        assertEquals(3L, result.get(1).longValue());
        assertEquals(1L, result.get(2).longValue());
    }

    private Pool createPoolWithBalance(final Long balance) {
        Pool pool = new Pool();
        pool.setBalance(balance);
        return pool;
    }

    @Test
    public void getCurrentExchange_withNoPoolPresent() throws Exception {
        when(repo.findByYearMonth(anyString())).thenReturn(Optional.empty());

        ResponseEntity responseEntity = sut.getCurrentExchange();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(0L, responseEntity.getBody());
    }

    @Test
    public void getCurrentExchange_withNoCoinsClaimed() throws Exception {
        when(repo.findByYearMonth(anyString())).thenReturn(Optional.of(new Pool()));

        ResponseEntity responseEntity = sut.getCurrentExchange();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(0L, responseEntity.getBody());
    }

    @Test
    public void getCurrentExchange() throws Exception {
        Pool pool = new Pool();
        pool.setBalance(2L);
        pool.setClaimedCoins(2L);
        when(repo.findByYearMonth(anyString())).thenReturn(Optional.of(pool));

        ResponseEntity responseEntity = sut.getCurrentExchange();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1L, responseEntity.getBody());
    }

    @Test
    public void getCurrentExchange_b() throws Exception {
        Pool pool = new Pool();
        pool.setBalance(2L);
        pool.setClaimedCoins(1L);
        when(repo.findByYearMonth(anyString())).thenReturn(Optional.of(pool));

        ResponseEntity responseEntity = sut.getCurrentExchange();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(2L, responseEntity.getBody());
    }
}
