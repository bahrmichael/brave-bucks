package com.bravebucks.eve.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;

import com.bravebucks.eve.domain.Donation;
import com.bravebucks.eve.domain.User;
import com.bravebucks.eve.repository.CharacterRepository;
import com.bravebucks.eve.repository.DonationRepository;
import com.bravebucks.eve.repository.RattingEntryRepository;
import com.bravebucks.eve.repository.UserRepository;
import com.bravebucks.eve.domain.Killmail;
import com.bravebucks.eve.repository.KillmailRepository;
import com.bravebucks.eve.repository.TransactionRepository;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

public class PayoutCalculatorTest {

    private KillmailRepository killmailRepo = mock(KillmailRepository.class);
    private UserRepository userRepo = mock(UserRepository.class);
    private DonationRepository donationRepo = mock(DonationRepository.class);
    private TransactionRepository transactionRepo = mock(TransactionRepository.class);
    private RattingEntryRepository rattingEntryRepository = mock(RattingEntryRepository.class);
    private CharacterRepository characterRepository = mock(CharacterRepository.class);
    private PayoutCalculator sut = new PayoutCalculator(killmailRepo, userRepo, transactionRepo,
                                                        rattingEntryRepository, characterRepository, null);

    @Test
    public void calculatePayouts() {
        User user = new User();
        user.setCharacterId(1L);
        user.setLogin("test");
        when(userRepo.findAll()).thenReturn(Collections.singletonList(user));
        final Killmail pendingKillmail = new Killmail();
        pendingKillmail.setPoints(1L);
        pendingKillmail.setAttackerIds(Collections.singletonList(user.getCharacterId()));
        when(killmailRepo.findPending()).thenReturn(Collections.singletonList(pendingKillmail));
        when(transactionRepo.save(anyList())).thenReturn(null);
        when(killmailRepo.save(any(Killmail.class))).thenReturn(null);
        LocalDate now = LocalDate.now();
        final Donation donation = new Donation();
        donation.setAmount(1.0);
        donation.setCreated(Instant.now());
        when(donationRepo.findByMonth(now.getYear() + "-" + now.getMonthValue())).thenReturn(Collections.singletonList(donation));

        sut.calculatePayouts();

        verify(killmailRepo).save(anyList());
        verify(transactionRepo).save(anyList());
    }

    @Test
    public void shouldHavePayoutsWith1DigitMonths() {
        final Donation donation = new Donation();
        donation.setCreated(LocalDate.of(2017, 12, 1).atStartOfDay().toInstant(ZoneOffset.UTC));
        donation.amount(1000000.0);

        int remainingWorth = (int) sut.getRemainingWorth(donation, LocalDate.of(2018, 1, 5));

        assertEquals(32258, remainingWorth);
    }
}
