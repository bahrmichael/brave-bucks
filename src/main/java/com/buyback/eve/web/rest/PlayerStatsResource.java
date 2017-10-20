package com.buyback.eve.web.rest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.buyback.eve.domain.Killmail;
import com.buyback.eve.domain.Payout;
import com.buyback.eve.domain.Transaction;
import com.buyback.eve.domain.User;
import com.buyback.eve.repository.KillmailRepository;
import com.buyback.eve.repository.PayoutRepository;
import com.buyback.eve.repository.TransactionRepository;
import com.buyback.eve.repository.UserRepository;
import com.buyback.eve.security.SecurityUtils;
import com.buyback.eve.web.dto.KillmailDto;
import static com.buyback.eve.domain.enumeration.PayoutStatus.REQUESTED;
import static com.buyback.eve.service.KillmailParser.calculateCoins;

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

    public PlayerStatsResource(final KillmailRepository killmailRepository,
                               final UserRepository userRepository,
                               final TransactionRepository transactionRepository,
                               final PayoutRepository payoutRepository) {
        this.killmailRepository = killmailRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.payoutRepository = payoutRepository;
    }

    @GetMapping(path = "/stats/potentialPayout")
    public ResponseEntity<Double> getPotentialPayout() {
        final double sum = transactionRepository.findAllByUser(SecurityUtils.getCurrentUserLogin()).stream()
                                                .mapToDouble(Transaction::getAmount).sum()
                           - payoutRepository.findAllByUserAndStatus(SecurityUtils.getCurrentUserLogin(), REQUESTED)
                          .stream().mapToDouble(Payout::getAmount).sum();
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
                                                   .map(mail -> createMailDto(mail, oneByLogin.get().getCharacterId()))
                                                   .collect(Collectors.toList()));
    }

    private KillmailDto createMailDto(final Killmail mail, final long characterId) {
        final KillmailDto dto = new KillmailDto();
        dto.setCoins(calculateCoins(mail, characterId));
        dto.setKillmailId(mail.getKillId());
        dto.setKillTime(mail.getKillTime());
        dto.setVictimAlliance(mail.getVictimAlliance());
        return dto;
    }
}
