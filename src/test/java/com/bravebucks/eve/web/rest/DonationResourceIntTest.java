package com.bravebucks.eve.web.rest;

import com.bravebucks.eve.BraveBucksApp;

import com.bravebucks.eve.domain.Donation;
import com.bravebucks.eve.repository.DonationRepository;
import com.bravebucks.eve.web.rest.errors.ExceptionTranslator;

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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the DonationResource REST controller.
 *
 * @see DonationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BraveBucksApp.class)
@ContextConfiguration(initializers = EnvironmentTestConfiguration.class)
public class DonationResourceIntTest {

    private static final String DEFAULT_DONATER = "AAAAAAAAAA";
    private static final String UPDATED_DONATER = "BBBBBBBBBB";

    private static final String DEFAULT_MONTH = "2012-10";
    private static final String UPDATED_MONTH = "2015-01";

    private static final Double DEFAULT_AMOUNT = 1D;
    private static final Double UPDATED_AMOUNT = 2D;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restDonationMockMvc;

    private Donation donation;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DonationResource donationResource = new DonationResource(donationRepository);
        this.restDonationMockMvc = MockMvcBuilders.standaloneSetup(donationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Donation createEntity() {
        Donation donation = new Donation()
            .donater(DEFAULT_DONATER)
            .month(DEFAULT_MONTH)
            .amount(DEFAULT_AMOUNT);
        return donation;
    }

    @Before
    public void initTest() {
        donationRepository.deleteAll();
        donation = createEntity();
    }

    @Test
    public void createDonation() throws Exception {
        int databaseSizeBeforeCreate = donationRepository.findAll().size();

        // Create the Donation
        restDonationMockMvc.perform(post("/api/donations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(donation)))
            .andExpect(status().isCreated());

        // Validate the Donation in the database
        List<Donation> donationList = donationRepository.findAll();
        assertThat(donationList).hasSize(databaseSizeBeforeCreate + 1);
        Donation testDonation = donationList.get(donationList.size() - 1);
        assertThat(testDonation.getDonater()).isEqualTo(DEFAULT_DONATER);
        assertThat(testDonation.getMonth()).isEqualTo(DEFAULT_MONTH);
        assertThat(testDonation.getAmount()).isEqualTo(DEFAULT_AMOUNT);
    }

    @Test
    public void createDonationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = donationRepository.findAll().size();

        // Create the Donation with an existing ID
        donation.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restDonationMockMvc.perform(post("/api/donations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(donation)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Donation> donationList = donationRepository.findAll();
        assertThat(donationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllDonations() throws Exception {
        // Initialize the database
        donationRepository.save(donation);

        // Get all the donationList
        restDonationMockMvc.perform(get("/api/donations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(donation.getId())))
            .andExpect(jsonPath("$.[*].donater").value(hasItem(DEFAULT_DONATER.toString())))
            .andExpect(jsonPath("$.[*].month").value(hasItem(DEFAULT_MONTH.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())));
    }

    @Test
    public void getDonation() throws Exception {
        // Initialize the database
        donationRepository.save(donation);

        // Get the donation
        restDonationMockMvc.perform(get("/api/donations/{id}", donation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(donation.getId()))
            .andExpect(jsonPath("$.donater").value(DEFAULT_DONATER.toString()))
            .andExpect(jsonPath("$.month").value(DEFAULT_MONTH.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.doubleValue()));
    }

    @Test
    public void getNonExistingDonation() throws Exception {
        // Get the donation
        restDonationMockMvc.perform(get("/api/donations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateDonation() throws Exception {
        // Initialize the database
        donationRepository.save(donation);
        int databaseSizeBeforeUpdate = donationRepository.findAll().size();

        // Update the donation
        Donation updatedDonation = donationRepository.findOne(donation.getId());
        updatedDonation
            .donater(UPDATED_DONATER)
            .month(UPDATED_MONTH)
            .amount(UPDATED_AMOUNT);

        restDonationMockMvc.perform(put("/api/donations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedDonation)))
            .andExpect(status().isOk());

        // Validate the Donation in the database
        List<Donation> donationList = donationRepository.findAll();
        assertThat(donationList).hasSize(databaseSizeBeforeUpdate);
        Donation testDonation = donationList.get(donationList.size() - 1);
        assertThat(testDonation.getDonater()).isEqualTo(UPDATED_DONATER);
        assertThat(testDonation.getMonth()).isEqualTo(UPDATED_MONTH);
        assertThat(testDonation.getAmount()).isEqualTo(UPDATED_AMOUNT);
    }

    @Test
    public void updateNonExistingDonation() throws Exception {
        int databaseSizeBeforeUpdate = donationRepository.findAll().size();

        // Create the Donation

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restDonationMockMvc.perform(put("/api/donations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(donation)))
            .andExpect(status().isCreated());

        // Validate the Donation in the database
        List<Donation> donationList = donationRepository.findAll();
        assertThat(donationList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    public void deleteDonation() throws Exception {
        // Initialize the database
        donationRepository.save(donation);
        int databaseSizeBeforeDelete = donationRepository.findAll().size();

        // Get the donation
        restDonationMockMvc.perform(delete("/api/donations/{id}", donation.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Donation> donationList = donationRepository.findAll();
        assertThat(donationList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Donation.class);
        Donation donation1 = new Donation();
        donation1.setId("id1");
        Donation donation2 = new Donation();
        donation2.setId(donation1.getId());
        assertThat(donation1).isEqualTo(donation2);
        donation2.setId("id2");
        assertThat(donation1).isNotEqualTo(donation2);
        donation1.setId(null);
        assertThat(donation1).isNotEqualTo(donation2);
    }
}
