package com.buyback.eve.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import com.buyback.eve.domain.Donation;
import com.buyback.eve.domain.Killmail;
import com.buyback.eve.domain.Transaction;
import com.buyback.eve.domain.User;
import com.buyback.eve.domain.enumeration.TransactionType;
import com.buyback.eve.repository.DonationRepository;
import com.buyback.eve.repository.KillmailRepository;
import com.buyback.eve.repository.TransactionRepository;
import com.buyback.eve.repository.UserRepository;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

/**
 * PayoutCalculatorTest
 *
 * Created on 21.10.2017
 *
 * Copyright (C) 2017 Volkswagen AG, All rights reserved.
 */
public class PayoutCalculatorTest {

    private KillmailRepository killmailRepo = mock(KillmailRepository.class);
    private UserRepository userRepo = mock(UserRepository.class);
    private DonationRepository donationRepo = mock(DonationRepository.class);
    private TransactionRepository transactionRepo = mock(TransactionRepository.class);
    private PayoutCalculator sut = new PayoutCalculator(killmailRepo, userRepo, donationRepo, transactionRepo);

    @Test
    public void calculatePayouts() throws Exception {
        User user = new User();
        user.setCharacterId(1L);
        user.setLogin("test");
        when(userRepo.findAll()).thenReturn(Collections.singletonList(user));
        final Killmail pendingKillmail = new Killmail();
        pendingKillmail.setPoints(1L);
        pendingKillmail.setAttackerIds(Collections.singletonList(user.getCharacterId()));
        when(killmailRepo.findPending()).thenReturn(Collections.singletonList(pendingKillmail));
        when(transactionRepo.findAll()).thenReturn(Collections.emptyList());
        when(transactionRepo.save(anyList())).thenReturn(null);
        when(killmailRepo.save(any(Killmail.class))).thenReturn(null);
        LocalDate now = LocalDate.now();
        final Donation donation = new Donation();
        donation.setAmount(1.0);
        donation.setCreated(Instant.now());
        when(donationRepo.findByMonth(now.getYear() + "-" + now.getMonthValue())).thenReturn(Collections.singletonList(donation));

        sut.calculatePayouts();

        verify(killmailRepo).save(any(Killmail.class));
        verify(transactionRepo).save(anyList());
    }

    @Test
    public void deleteExistingKillIskFromToday() throws Exception {
        final Transaction transaction = new Transaction("test", 1.0, TransactionType.KILL);
        transaction.setInstant(Instant.now());
        when(transactionRepo.findAll()).thenReturn(Collections.singletonList(transaction));

        sut.deleteExistingKillIskFromToday();

        verify(transactionRepo).delete(transaction);
    }
}
