package com.bravebucks.eve.web.rest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bravebucks.eve.domain.Donation;
import com.bravebucks.eve.domain.Killmail;
import com.bravebucks.eve.domain.Payout;
import com.bravebucks.eve.domain.Transaction;
import com.bravebucks.eve.domain.User;
import com.bravebucks.eve.domain.enumeration.TransactionType;
import com.bravebucks.eve.repository.DonationRepository;
import com.bravebucks.eve.repository.KillmailRepository;
import com.bravebucks.eve.repository.PayoutRepository;
import com.bravebucks.eve.repository.TransactionRepository;
import com.bravebucks.eve.repository.UserRepository;
import com.bravebucks.eve.security.AuthoritiesConstants;
import com.bravebucks.eve.security.SecurityUtils;
import com.bravebucks.eve.domain.HighscoreEntry;
import com.bravebucks.eve.web.dto.KillmailDto;
import com.codahale.metrics.annotation.Timed;
import static com.bravebucks.eve.domain.enumeration.PayoutStatus.REQUESTED;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

@RestController
@RequestMapping("/api")
@Secured(AuthoritiesConstants.USER)
public class PlayerStatsResource {

    private final Logger log = LoggerFactory.getLogger(PlayerStatsResource.class);

    private final KillmailRepository killmailRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final PayoutRepository payoutRepository;
    private final DonationRepository donationRepository;
    private final MongoTemplate mongoTemplate ;

    public PlayerStatsResource(final KillmailRepository killmailRepository,
                               final UserRepository userRepository,
                               final TransactionRepository transactionRepository,
                               final PayoutRepository payoutRepository,
                               final DonationRepository donationRepository,
                               final MongoTemplate mongoTemplate) {
        this.killmailRepository = killmailRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.payoutRepository = payoutRepository;
        this.donationRepository = donationRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping(path = "/stats/highscore")
    public ResponseEntity<List<HighscoreEntry>> getHighscore() {
        Date date = Date.from(Instant.now().minus(30, ChronoUnit.DAYS));
        final MatchOperation filterByInstant = match(new Criteria("instant").gte(date));
        final MatchOperation filterByType = match(new Criteria("type").is(TransactionType.KILL.name()));
        final GroupOperation groupByStateAndSumPop = group("user").sum("amount").as("amount");
        final SortOperation sortByPopDesc = sort(new Sort(Sort.Direction.DESC, "amount"));
        final ProjectionOperation project = project().andExpression("_id").as("name")
                                                .andExpression("amount").as("amount");

        Aggregation aggregation = newAggregation(filterByInstant, filterByType, groupByStateAndSumPop, sortByPopDesc, project);
        AggregationResults<HighscoreEntry> result = mongoTemplate.aggregate(
            aggregation, "transaction", HighscoreEntry.class);

        List<HighscoreEntry> list = result.getMappedResults();
        if (list.size() > 10) {
            list = list.subList(0, 10);
        }
        return ResponseEntity.ok(list);
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
    @Timed
    public ResponseEntity<Double> getPotentialPayout() {
        final String user = SecurityUtils.getCurrentUserLogin();
        double transactions = 0 + transactionRepository.findAllByUser(user).stream()
                                                       .mapToDouble(Transaction::getAmount).sum();
        double pendingPayouts = 0 + payoutRepository.findAllByUserAndStatus(user, REQUESTED)
                          .stream().mapToDouble(Payout::getAmount).sum();
        final double sum = transactions - pendingPayouts;
        log.info("Potential Payout for {} is {}.", user, (int) sum);
        return ResponseEntity.ok(sum);
    }

    @GetMapping(path = "/killmails")
    @Timed
    public ResponseEntity getKillmails() {
        final String user = SecurityUtils.getCurrentUserLogin();
        final Optional<User> oneByLogin = userRepository.findOneByLogin(user);
        if (!oneByLogin.isPresent()) {
            return ResponseEntity.badRequest().body("Could not resolve user.");
        }
        final PageRequest pageRequest = new PageRequest(0, 10, Sort.Direction.DESC, "killTime");
        final List<KillmailDto> result = killmailRepository.findByAttackerId(oneByLogin.get().getCharacterId(), pageRequest)
                                                      .stream()
                                                      .map(PlayerStatsResource::createMailDto)
                                                      .collect(Collectors.toList());
        log.info("Returning {} killmails for {}.", result.size(), user);
        return ResponseEntity.ok(result);
    }

    public static KillmailDto createMailDto(final Killmail mail) {
        final KillmailDto dto = new KillmailDto();
        dto.setKillmailId(mail.getKillId());
        dto.setKillTime(Instant.parse(mail.getKillTime()));
        dto.setVictimAlliance(mail.getVictimGroupName());
        dto.setShipTypeId(mail.getShipTypeId());
        return dto;
    }
}
