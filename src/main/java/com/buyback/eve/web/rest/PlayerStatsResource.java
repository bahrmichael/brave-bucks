package com.buyback.eve.web.rest;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;

import com.buyback.eve.domain.Donation;
import com.buyback.eve.domain.Killmail;
import com.buyback.eve.domain.Payout;
import com.buyback.eve.domain.Transaction;
import com.buyback.eve.domain.User;
import com.buyback.eve.repository.DonationRepository;
import com.buyback.eve.repository.KillmailRepository;
import com.buyback.eve.repository.PayoutRepository;
import com.buyback.eve.repository.TransactionRepository;
import com.buyback.eve.repository.UserRepository;
import com.buyback.eve.security.SecurityUtils;
import com.buyback.eve.web.dto.KillmailDto;
import static com.buyback.eve.domain.enumeration.PayoutStatus.REQUESTED;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PlayerStatsResource {

    private final KillmailRepository killmailRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final PayoutRepository payoutRepository;
    private final DonationRepository donationRepository;

    public PlayerStatsResource(final KillmailRepository killmailRepository,
                               final UserRepository userRepository,
                               final TransactionRepository transactionRepository,
                               final PayoutRepository payoutRepository,
                               final DonationRepository donationRepository) {
        this.killmailRepository = killmailRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.payoutRepository = payoutRepository;
        this.donationRepository = donationRepository;
    }

    @GetMapping(path = "/stats/month-available")
    public ResponseEntity<Double> getMonthAvailable() {
        return ResponseEntity.ok(getPayable());
    }

    private double getPayable() {
        final LocalDate now = LocalDate.now();
        return donationRepository.findByMonth(now.getYear() + "-" + now.getMonthValue()).stream()
                                 .mapToDouble(Donation::getAmount).sum();
    }

    @GetMapping(path = "/stats/potentialPayout")
    public ResponseEntity<Double> getPotentialPayout() {
        double transactions = 0 + transactionRepository.findAllByUser(SecurityUtils.getCurrentUserLogin()).stream()
                                           .mapToDouble(Transaction::getAmount).sum();
        double payouts = 0 + payoutRepository.findAllByUserAndStatus(SecurityUtils.getCurrentUserLogin(), REQUESTED)
                          .stream().mapToDouble(Payout::getAmount).sum();
        final double sum = transactions - payouts;
        return ResponseEntity.ok(sum);
    }

    @GetMapping(path = "/killmails")
    public ResponseEntity getKillmails() {
        final Optional<User> oneByLogin = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
        if (!oneByLogin.isPresent()) {
            return ResponseEntity.badRequest().body("Could not resolve user.");
        }
        return ResponseEntity.ok(killmailRepository.findByAttackerId(oneByLogin.get().getCharacterId())
                                                   .stream()
                                                   .map(this::createMailDto)
                                                   .collect(Collectors.toList()));
    }

    private KillmailDto createMailDto(final Killmail mail) {
        final KillmailDto dto = new KillmailDto();
        dto.setKillmailId(mail.getKillId());
        dto.setKillTime(mail.getKillTime());
        dto.setVictimAlliance(mail.getVictimGroupName());
        return dto;
    }
}
