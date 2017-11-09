package com.bravebucks.eve.web.rest;

import java.util.List;

import com.bravebucks.eve.BraveBucksApp;
import com.bravebucks.eve.domain.AdRequest;
import com.bravebucks.eve.domain.enumeration.AdStatus;
import com.bravebucks.eve.repository.AdRequestRepository;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * Test class for the AdRequestResource REST controller.
 *
 * @see AdRequestResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BraveBucksApp.class)
@ContextConfiguration(initializers = EnvironmentTestConfiguration.class)
public class AdRequestResourceIntTest {

    private static final String DEFAULT_REQUESTER = "AAAAAAAAAA";
    private static final String UPDATED_REQUESTER = "BBBBBBBBBB";

    private static final String DEFAULT_SERVICE = "AAAAAAAAAA";
    private static final String UPDATED_SERVICE = "BBBBBBBBBB";

    private static final String DEFAULT_MONTH = "AAAAAAAAAA";
    private static final String UPDATED_MONTH = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_LINK = "AAAAAAAAAA";
    private static final String UPDATED_LINK = "BBBBBBBBBB";

    private static final AdStatus DEFAULT_AD_STATUS = AdStatus.REQUESTED;
    private static final AdStatus UPDATED_AD_STATUS = AdStatus.APPROVED;

    @Autowired
    private AdRequestRepository adRequestRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restAdRequestMockMvc;

    private AdRequest adRequest;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AdRequestResource adRequestResource = new AdRequestResource(adRequestRepository);
        this.restAdRequestMockMvc = MockMvcBuilders.standaloneSetup(adRequestResource)
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
    public static AdRequest createEntity() {
        AdRequest adRequest = new AdRequest()
            .requester(DEFAULT_REQUESTER)
            .service(DEFAULT_SERVICE)
            .month(DEFAULT_MONTH)
            .description(DEFAULT_DESCRIPTION)
            .link(DEFAULT_LINK)
            .adStatus(DEFAULT_AD_STATUS);
        return adRequest;
    }

    @Before
    public void initTest() {
        adRequestRepository.deleteAll();
        adRequest = createEntity();
    }

    @Test
    public void createAdRequest() throws Exception {
        int databaseSizeBeforeCreate = adRequestRepository.findAll().size();

        // Create the AdRequest
        restAdRequestMockMvc.perform(post("/api/ad-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(adRequest)))
            .andExpect(status().isCreated());

        // Validate the AdRequest in the database
        List<AdRequest> adRequestList = adRequestRepository.findAll();
        assertThat(adRequestList).hasSize(databaseSizeBeforeCreate + 1);
        AdRequest testAdRequest = adRequestList.get(adRequestList.size() - 1);
        assertThat(testAdRequest.getRequester()).isEqualTo(null);
        assertThat(testAdRequest.getService()).isEqualTo(DEFAULT_SERVICE);
        assertThat(testAdRequest.getMonth()).isEqualTo(DEFAULT_MONTH);
        assertThat(testAdRequest.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAdRequest.getLink()).isEqualTo(DEFAULT_LINK);
        assertThat(testAdRequest.getAdStatus()).isEqualTo(DEFAULT_AD_STATUS);
    }

    @Test
    public void createAdRequestWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = adRequestRepository.findAll().size();

        // Create the AdRequest with an existing ID
        adRequest.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restAdRequestMockMvc.perform(post("/api/ad-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(adRequest)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<AdRequest> adRequestList = adRequestRepository.findAll();
        assertThat(adRequestList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllAdRequests() throws Exception {
        // Initialize the database
        adRequestRepository.save(adRequest);

        // Get all the adRequestList
        restAdRequestMockMvc.perform(get("/api/ad-requests?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(adRequest.getId())))
            .andExpect(jsonPath("$.[*].requester").value(hasItem(DEFAULT_REQUESTER.toString())))
            .andExpect(jsonPath("$.[*].service").value(hasItem(DEFAULT_SERVICE.toString())))
            .andExpect(jsonPath("$.[*].month").value(hasItem(DEFAULT_MONTH.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].link").value(hasItem(DEFAULT_LINK.toString())))
            .andExpect(jsonPath("$.[*].adStatus").value(hasItem(DEFAULT_AD_STATUS.toString())));
    }

    @Test
    public void getAdRequest() throws Exception {
        // Initialize the database
        adRequestRepository.save(adRequest);

        // Get the adRequest
        restAdRequestMockMvc.perform(get("/api/ad-requests/{id}", adRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(adRequest.getId()))
            .andExpect(jsonPath("$.requester").value(DEFAULT_REQUESTER.toString()))
            .andExpect(jsonPath("$.service").value(DEFAULT_SERVICE.toString()))
            .andExpect(jsonPath("$.month").value(DEFAULT_MONTH.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.link").value(DEFAULT_LINK.toString()))
            .andExpect(jsonPath("$.adStatus").value(DEFAULT_AD_STATUS.toString()));
    }

    @Test
    public void getNonExistingAdRequest() throws Exception {
        // Get the adRequest
        restAdRequestMockMvc.perform(get("/api/ad-requests/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateAdRequest() throws Exception {
        // Initialize the database
        adRequestRepository.save(adRequest);
        int databaseSizeBeforeUpdate = adRequestRepository.findAll().size();

        // Update the adRequest
        AdRequest updatedAdRequest = adRequestRepository.findOne(adRequest.getId());
        updatedAdRequest
            .requester(UPDATED_REQUESTER)
            .service(UPDATED_SERVICE)
            .month(UPDATED_MONTH)
            .description(UPDATED_DESCRIPTION)
            .link(UPDATED_LINK)
            .adStatus(UPDATED_AD_STATUS);

        restAdRequestMockMvc.perform(put("/api/ad-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAdRequest)))
            .andExpect(status().isOk());

        // Validate the AdRequest in the database
        List<AdRequest> adRequestList = adRequestRepository.findAll();
        assertThat(adRequestList).hasSize(databaseSizeBeforeUpdate);
        AdRequest testAdRequest = adRequestList.get(adRequestList.size() - 1);
        assertThat(testAdRequest.getRequester()).isEqualTo(UPDATED_REQUESTER);
        assertThat(testAdRequest.getService()).isEqualTo(UPDATED_SERVICE);
        assertThat(testAdRequest.getMonth()).isEqualTo(UPDATED_MONTH);
        assertThat(testAdRequest.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAdRequest.getLink()).isEqualTo(UPDATED_LINK);
        assertThat(testAdRequest.getAdStatus()).isEqualTo(UPDATED_AD_STATUS);
    }

    @Test
    public void updateNonExistingAdRequest() throws Exception {
        int databaseSizeBeforeUpdate = adRequestRepository.findAll().size();

        // Create the AdRequest

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restAdRequestMockMvc.perform(put("/api/ad-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(adRequest)))
            .andExpect(status().isCreated());

        // Validate the AdRequest in the database
        List<AdRequest> adRequestList = adRequestRepository.findAll();
        assertThat(adRequestList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    public void deleteAdRequest() throws Exception {
        // Initialize the database
        adRequestRepository.save(adRequest);
        int databaseSizeBeforeDelete = adRequestRepository.findAll().size();

        // Get the adRequest
        restAdRequestMockMvc.perform(delete("/api/ad-requests/{id}", adRequest.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<AdRequest> adRequestList = adRequestRepository.findAll();
        assertThat(adRequestList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AdRequest.class);
        AdRequest adRequest1 = new AdRequest();
        adRequest1.setId("id1");
        AdRequest adRequest2 = new AdRequest();
        adRequest2.setId(adRequest1.getId());
        assertThat(adRequest1).isEqualTo(adRequest2);
        adRequest2.setId("id2");
        assertThat(adRequest1).isNotEqualTo(adRequest2);
        adRequest1.setId(null);
        assertThat(adRequest1).isNotEqualTo(adRequest2);
    }
}
