package com.bravebucks.eve.web.rest;

import com.bravebucks.eve.BraveBucksApp;

import com.bravebucks.eve.domain.SolarSystem;
import com.bravebucks.eve.repository.SolarSystemRepository;
import com.bravebucks.eve.service.JsonRequestService;
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
 * Test class for the SolarSystemResource REST controller.
 *
 * @see SolarSystemResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BraveBucksApp.class)
@ContextConfiguration(initializers = EnvironmentTestConfiguration.class)
public class SolarSystemResourceIntTest {

    private static final Long DEFAULT_SYSTEM_ID = 30001198L;

    private static final String DEFAULT_SYSTEM_NAME = "GE-8JV";

    @Autowired
    private SolarSystemRepository solarSystemRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private JsonRequestService jsonRequestService;

    private MockMvc restSolarSystemMockMvc;

    private SolarSystem solarSystem;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SolarSystemResource solarSystemResource = new SolarSystemResource(solarSystemRepository,
                                                                                jsonRequestService);
        this.restSolarSystemMockMvc = MockMvcBuilders.standaloneSetup(solarSystemResource)
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
    public static SolarSystem createEntity() {
        SolarSystem solarSystem = new SolarSystem()
            .systemId(DEFAULT_SYSTEM_ID)
            .systemName(DEFAULT_SYSTEM_NAME);
        return solarSystem;
    }

    @Before
    public void initTest() {
        solarSystemRepository.deleteAll();
        solarSystem = createEntity();
    }

    @Test
    public void createSolarSystem() throws Exception {
        int databaseSizeBeforeCreate = solarSystemRepository.findAll().size();

        // Create the SolarSystem
        restSolarSystemMockMvc.perform(post("/api/solar-systems")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solarSystem)))
            .andExpect(status().isCreated());

        // Validate the SolarSystem in the database
        List<SolarSystem> solarSystemList = solarSystemRepository.findAll();
        assertThat(solarSystemList).hasSize(databaseSizeBeforeCreate + 1);
        SolarSystem testSolarSystem = solarSystemList.get(solarSystemList.size() - 1);
        assertThat(testSolarSystem.getSystemId()).isEqualTo(DEFAULT_SYSTEM_ID);
        assertThat(testSolarSystem.getSystemName()).isEqualTo(DEFAULT_SYSTEM_NAME);
    }

    @Test
    public void createSolarSystemWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = solarSystemRepository.findAll().size();

        // Create the SolarSystem with an existing ID
        solarSystem.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restSolarSystemMockMvc.perform(post("/api/solar-systems")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solarSystem)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<SolarSystem> solarSystemList = solarSystemRepository.findAll();
        assertThat(solarSystemList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllSolarSystems() throws Exception {
        // Initialize the database
        solarSystemRepository.save(solarSystem);

        // Get all the solarSystemList
        restSolarSystemMockMvc.perform(get("/api/solar-systems?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(solarSystem.getId())))
            .andExpect(jsonPath("$.[*].systemId").value(hasItem(DEFAULT_SYSTEM_ID.intValue())))
            .andExpect(jsonPath("$.[*].systemName").value(hasItem(DEFAULT_SYSTEM_NAME.toString())));
    }

    @Test
    public void getSolarSystem() throws Exception {
        // Initialize the database
        solarSystemRepository.save(solarSystem);

        // Get the solarSystem
        restSolarSystemMockMvc.perform(get("/api/solar-systems/{id}", solarSystem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(solarSystem.getId()))
            .andExpect(jsonPath("$.systemId").value(DEFAULT_SYSTEM_ID.intValue()))
            .andExpect(jsonPath("$.systemName").value(DEFAULT_SYSTEM_NAME.toString()));
    }

    @Test
    public void getNonExistingSolarSystem() throws Exception {
        // Get the solarSystem
        restSolarSystemMockMvc.perform(get("/api/solar-systems/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void deleteSolarSystem() throws Exception {
        // Initialize the database
        solarSystemRepository.save(solarSystem);
        int databaseSizeBeforeDelete = solarSystemRepository.findAll().size();

        // Get the solarSystem
        restSolarSystemMockMvc.perform(delete("/api/solar-systems/{id}", solarSystem.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<SolarSystem> solarSystemList = solarSystemRepository.findAll();
        assertThat(solarSystemList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SolarSystem.class);
        SolarSystem solarSystem1 = new SolarSystem();
        solarSystem1.setId("id1");
        SolarSystem solarSystem2 = new SolarSystem();
        solarSystem2.setId(solarSystem1.getId());
        assertThat(solarSystem1).isEqualTo(solarSystem2);
        solarSystem2.setId("id2");
        assertThat(solarSystem1).isNotEqualTo(solarSystem2);
        solarSystem1.setId(null);
        assertThat(solarSystem1).isNotEqualTo(solarSystem2);
    }
}
