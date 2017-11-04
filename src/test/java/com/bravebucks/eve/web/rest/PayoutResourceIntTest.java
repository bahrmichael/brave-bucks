package com.bravebucks.eve.web.rest;

import com.bravebucks.eve.BraveBucksApp;
import com.bravebucks.eve.domain.Payout;
import com.bravebucks.eve.web.rest.errors.ExceptionTranslator;

import com.bravebucks.eve.repository.PayoutRepository;
import com.bravebucks.eve.repository.TransactionRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bravebucks.eve.domain.enumeration.PayoutStatus;

/**
 * Test class for the PayoutResource REST controller.
 *
 * @see PayoutResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BraveBucksApp.class)
@ContextConfiguration(initializers = EnvironmentTestConfiguration.class)
public class PayoutResourceIntTest {

    private static final String DEFAULT_USER = "AAAAAAAAAA";
    private static final String UPDATED_USER = "BBBBBBBBBB";

    private static final Double DEFAULT_AMOUNT = 1D;
    private static final Double UPDATED_AMOUNT = 2D;

    private static final Instant DEFAULT_LAST_UPDATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_UPDATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final PayoutStatus DEFAULT_STATUS = PayoutStatus.REQUESTED;
    private static final PayoutStatus UPDATED_STATUS = PayoutStatus.PAID;

    private static final String DEFAULT_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_DETAILS = "BBBBBBBBBB";

    @Autowired
    private PayoutRepository payoutRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restPayoutMockMvc;

    private Payout payout;

    @Autowired
    private TransactionRepository transactionRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PayoutResource payoutResource = new PayoutResource(payoutRepository, transactionRepository);
        restPayoutMockMvc = MockMvcBuilders.standaloneSetup(payoutResource)
                                           .setCustomArgumentResolvers(pageableArgumentResolver)
                                           .setControllerAdvice(exceptionTranslator)
                                           .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it, if they test an entity which requires
     * the current entity.
     */
    public static Payout createEntity() {
        Payout payout = new Payout()
            .user(DEFAULT_USER)
            .amount(DEFAULT_AMOUNT)
            .lastUpdated(DEFAULT_LAST_UPDATED)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .status(DEFAULT_STATUS)
            .details(DEFAULT_DETAILS);
        return payout;
    }

    @Before
    public void initTest() {
        payoutRepository.deleteAll();
        payout = createEntity();
    }

    @Test
    public void createPayout() throws Exception {
        int databaseSizeBeforeCreate = payoutRepository.findAll().size();

        // Create the Payout
        restPayoutMockMvc.perform(post("/api/payouts")
                                      .contentType(TestUtil.APPLICATION_JSON_UTF8)
                                      .content(TestUtil.convertObjectToJsonBytes(payout)))
                         .andExpect(status().isCreated());

        // Validate the Payout in the database
        List<Payout> payoutList = payoutRepository.findAll();
        assertThat(payoutList).hasSize(databaseSizeBeforeCreate + 1);
        Payout testPayout = payoutList.get(payoutList.size() - 1);
        assertThat(testPayout.getUser()).isEqualTo(DEFAULT_USER);
        assertThat(testPayout.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testPayout.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPayout.getDetails()).isEqualTo(DEFAULT_DETAILS);
    }

    @Test
    public void createPayoutWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = payoutRepository.findAll().size();

        // Create the Payout with an existing ID
        payout.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restPayoutMockMvc.perform(post("/api/payouts")
                                      .contentType(TestUtil.APPLICATION_JSON_UTF8)
                                      .content(TestUtil.convertObjectToJsonBytes(payout)))
                         .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Payout> payoutList = payoutRepository.findAll();
        assertThat(payoutList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllPayouts() throws Exception {
        // Initialize the database
        payoutRepository.save(payout);

        // Get all the payoutList
        restPayoutMockMvc.perform(get("/api/payouts?sort=id,desc"))
                         .andExpect(status().isOk())
                         .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                         .andExpect(jsonPath("$.[*].id").value(hasItem(payout.getId())))
                         .andExpect(jsonPath("$.[*].user").value(hasItem(DEFAULT_USER.toString())))
                         .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
                         .andExpect(jsonPath("$.[*].lastUpdated").value(hasItem(DEFAULT_LAST_UPDATED.toString())))
                         .andExpect(
                             jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY.toString())))
                         .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                         .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS.toString())));
    }

    @Test
    public void getPayout() throws Exception {
        // Initialize the database
        payoutRepository.save(payout);

        // Get the payout
        restPayoutMockMvc.perform(get("/api/payouts/{id}", payout.getId()))
                         .andExpect(status().isOk())
                         .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                         .andExpect(jsonPath("$.id").value(payout.getId()))
                         .andExpect(jsonPath("$.user").value(DEFAULT_USER.toString()))
                         .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.doubleValue()))
                         .andExpect(jsonPath("$.lastUpdated").value(DEFAULT_LAST_UPDATED.toString()))
                         .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY.toString()))
                         .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
                         .andExpect(jsonPath("$.details").value(DEFAULT_DETAILS.toString()));
    }

    @Test
    public void getNonExistingPayout() throws Exception {
        // Get the payout
        restPayoutMockMvc.perform(get("/api/payouts/{id}", Long.MAX_VALUE))
                         .andExpect(status().isNotFound());
    }

    @Test
    public void updatePayout() throws Exception {
        // Initialize the database
        payoutRepository.save(payout);
        int databaseSizeBeforeUpdate = payoutRepository.findAll().size();

        // Update the payout
        Payout updatedPayout = payoutRepository.findOne(payout.getId());
        updatedPayout
            .user(UPDATED_USER)
            .amount(UPDATED_AMOUNT)
            .status(UPDATED_STATUS)
            .details(UPDATED_DETAILS);

        restPayoutMockMvc.perform(put("/api/payouts")
                                      .contentType(TestUtil.APPLICATION_JSON_UTF8)
                                      .content(TestUtil.convertObjectToJsonBytes(updatedPayout)))
                         .andExpect(status().isOk());

        // Validate the Payout in the database
        List<Payout> payoutList = payoutRepository.findAll();
        assertThat(payoutList).hasSize(databaseSizeBeforeUpdate);
        Payout testPayout = payoutList.get(payoutList.size() - 1);
        assertThat(testPayout.getUser()).isEqualTo(UPDATED_USER);
        assertThat(testPayout.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testPayout.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPayout.getDetails()).isEqualTo(UPDATED_DETAILS);
    }

    @Test
    public void updatePayoutFailsIfAlreadyPaid() throws Exception {
        // Initialize the database
        Payout aPayout = new Payout("aUser", 1.0, null, PayoutStatus.PAID, null);
        payoutRepository.save(aPayout);
        int databaseSizeBeforeUpdate = payoutRepository.findAll().size();

        // Update the payout
        Payout updatedPayout = payoutRepository.findOne(aPayout.getId());
        updatedPayout.status(PayoutStatus.CANCELLED);

        restPayoutMockMvc.perform(put("/api/payouts")
                                      .contentType(TestUtil.APPLICATION_JSON_UTF8)
                                      .content(TestUtil.convertObjectToJsonBytes(updatedPayout)))
                         .andExpect(status().isBadRequest());
    }

    @Test
    public void updateNonExistingPayout() throws Exception {
        int databaseSizeBeforeUpdate = payoutRepository.findAll().size();

        // Create the Payout

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPayoutMockMvc.perform(put("/api/payouts")
                                      .contentType(TestUtil.APPLICATION_JSON_UTF8)
                                      .content(TestUtil.convertObjectToJsonBytes(payout)))
                         .andExpect(status().isCreated());

        // Validate the Payout in the database
        List<Payout> payoutList = payoutRepository.findAll();
        assertThat(payoutList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    public void deletePayout() throws Exception {
        // Initialize the database
        payoutRepository.save(payout);
        int databaseSizeBeforeDelete = payoutRepository.findAll().size();

        // Get the payout
        restPayoutMockMvc.perform(delete("/api/payouts/{id}", payout.getId())
                                      .accept(TestUtil.APPLICATION_JSON_UTF8))
                         .andExpect(status().isOk());

        // Validate the database is empty
        List<Payout> payoutList = payoutRepository.findAll();
        assertThat(payoutList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Payout.class);
        Payout payout1 = new Payout();
        payout1.setId("id1");
        Payout payout2 = new Payout();
        payout2.setId(payout1.getId());
        assertThat(payout1).isEqualTo(payout2);
        payout2.setId("id2");
        assertThat(payout1).isNotEqualTo(payout2);
        payout1.setId(null);
        assertThat(payout1).isNotEqualTo(payout2);
    }
}
