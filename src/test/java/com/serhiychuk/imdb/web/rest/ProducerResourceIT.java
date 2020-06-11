package com.serhiychuk.imdb.web.rest;

import com.serhiychuk.imdb.ImdbApp;
import com.serhiychuk.imdb.domain.Producer;
import com.serhiychuk.imdb.domain.Episode;
import com.serhiychuk.imdb.domain.Movie;
import com.serhiychuk.imdb.repository.ProducerRepository;
import com.serhiychuk.imdb.service.ProducerService;
import com.serhiychuk.imdb.service.dto.ProducerCriteria;
import com.serhiychuk.imdb.service.ProducerQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ProducerResource} REST controller.
 */
@SpringBootTest(classes = ImdbApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class ProducerResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SURNAME = "AAAAAAAAAA";
    private static final String UPDATED_SURNAME = "BBBBBBBBBB";

    private static final String DEFAULT_BIO = "AAAAAAAAAA";
    private static final String UPDATED_BIO = "BBBBBBBBBB";

    private static final String DEFAULT_PHOTO_URL = "AAAAAAAAAA";
    private static final String UPDATED_PHOTO_URL = "BBBBBBBBBB";

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private ProducerService producerService;

    @Autowired
    private ProducerQueryService producerQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProducerMockMvc;

    private Producer producer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Producer createEntity(EntityManager em) {
        Producer producer = new Producer()
            .name(DEFAULT_NAME)
            .surname(DEFAULT_SURNAME)
            .bio(DEFAULT_BIO)
            .photoURL(DEFAULT_PHOTO_URL);
        return producer;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Producer createUpdatedEntity(EntityManager em) {
        Producer producer = new Producer()
            .name(UPDATED_NAME)
            .surname(UPDATED_SURNAME)
            .bio(UPDATED_BIO)
            .photoURL(UPDATED_PHOTO_URL);
        return producer;
    }

    @BeforeEach
    public void initTest() {
        producer = createEntity(em);
    }

    @Test
    @Transactional
    public void createProducer() throws Exception {
        int databaseSizeBeforeCreate = producerRepository.findAll().size();

        // Create the Producer
        restProducerMockMvc.perform(post("/api/producers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(producer)))
            .andExpect(status().isCreated());

        // Validate the Producer in the database
        List<Producer> producerList = producerRepository.findAll();
        assertThat(producerList).hasSize(databaseSizeBeforeCreate + 1);
        Producer testProducer = producerList.get(producerList.size() - 1);
        assertThat(testProducer.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProducer.getSurname()).isEqualTo(DEFAULT_SURNAME);
        assertThat(testProducer.getBio()).isEqualTo(DEFAULT_BIO);
        assertThat(testProducer.getPhotoURL()).isEqualTo(DEFAULT_PHOTO_URL);
    }

    @Test
    @Transactional
    public void createProducerWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = producerRepository.findAll().size();

        // Create the Producer with an existing ID
        producer.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProducerMockMvc.perform(post("/api/producers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(producer)))
            .andExpect(status().isBadRequest());

        // Validate the Producer in the database
        List<Producer> producerList = producerRepository.findAll();
        assertThat(producerList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = producerRepository.findAll().size();
        // set the field null
        producer.setName(null);

        // Create the Producer, which fails.

        restProducerMockMvc.perform(post("/api/producers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(producer)))
            .andExpect(status().isBadRequest());

        List<Producer> producerList = producerRepository.findAll();
        assertThat(producerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSurnameIsRequired() throws Exception {
        int databaseSizeBeforeTest = producerRepository.findAll().size();
        // set the field null
        producer.setSurname(null);

        // Create the Producer, which fails.

        restProducerMockMvc.perform(post("/api/producers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(producer)))
            .andExpect(status().isBadRequest());

        List<Producer> producerList = producerRepository.findAll();
        assertThat(producerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProducers() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList
        restProducerMockMvc.perform(get("/api/producers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(producer.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].surname").value(hasItem(DEFAULT_SURNAME)))
            .andExpect(jsonPath("$.[*].bio").value(hasItem(DEFAULT_BIO)))
            .andExpect(jsonPath("$.[*].photoURL").value(hasItem(DEFAULT_PHOTO_URL)));
    }
    
    @Test
    @Transactional
    public void getProducer() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get the producer
        restProducerMockMvc.perform(get("/api/producers/{id}", producer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(producer.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.surname").value(DEFAULT_SURNAME))
            .andExpect(jsonPath("$.bio").value(DEFAULT_BIO))
            .andExpect(jsonPath("$.photoURL").value(DEFAULT_PHOTO_URL));
    }


    @Test
    @Transactional
    public void getProducersByIdFiltering() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        Long id = producer.getId();

        defaultProducerShouldBeFound("id.equals=" + id);
        defaultProducerShouldNotBeFound("id.notEquals=" + id);

        defaultProducerShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProducerShouldNotBeFound("id.greaterThan=" + id);

        defaultProducerShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProducerShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllProducersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where name equals to DEFAULT_NAME
        defaultProducerShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the producerList where name equals to UPDATED_NAME
        defaultProducerShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllProducersByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where name not equals to DEFAULT_NAME
        defaultProducerShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the producerList where name not equals to UPDATED_NAME
        defaultProducerShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllProducersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where name in DEFAULT_NAME or UPDATED_NAME
        defaultProducerShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the producerList where name equals to UPDATED_NAME
        defaultProducerShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllProducersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where name is not null
        defaultProducerShouldBeFound("name.specified=true");

        // Get all the producerList where name is null
        defaultProducerShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllProducersByNameContainsSomething() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where name contains DEFAULT_NAME
        defaultProducerShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the producerList where name contains UPDATED_NAME
        defaultProducerShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllProducersByNameNotContainsSomething() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where name does not contain DEFAULT_NAME
        defaultProducerShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the producerList where name does not contain UPDATED_NAME
        defaultProducerShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllProducersBySurnameIsEqualToSomething() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where surname equals to DEFAULT_SURNAME
        defaultProducerShouldBeFound("surname.equals=" + DEFAULT_SURNAME);

        // Get all the producerList where surname equals to UPDATED_SURNAME
        defaultProducerShouldNotBeFound("surname.equals=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    public void getAllProducersBySurnameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where surname not equals to DEFAULT_SURNAME
        defaultProducerShouldNotBeFound("surname.notEquals=" + DEFAULT_SURNAME);

        // Get all the producerList where surname not equals to UPDATED_SURNAME
        defaultProducerShouldBeFound("surname.notEquals=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    public void getAllProducersBySurnameIsInShouldWork() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where surname in DEFAULT_SURNAME or UPDATED_SURNAME
        defaultProducerShouldBeFound("surname.in=" + DEFAULT_SURNAME + "," + UPDATED_SURNAME);

        // Get all the producerList where surname equals to UPDATED_SURNAME
        defaultProducerShouldNotBeFound("surname.in=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    public void getAllProducersBySurnameIsNullOrNotNull() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where surname is not null
        defaultProducerShouldBeFound("surname.specified=true");

        // Get all the producerList where surname is null
        defaultProducerShouldNotBeFound("surname.specified=false");
    }
                @Test
    @Transactional
    public void getAllProducersBySurnameContainsSomething() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where surname contains DEFAULT_SURNAME
        defaultProducerShouldBeFound("surname.contains=" + DEFAULT_SURNAME);

        // Get all the producerList where surname contains UPDATED_SURNAME
        defaultProducerShouldNotBeFound("surname.contains=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    public void getAllProducersBySurnameNotContainsSomething() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where surname does not contain DEFAULT_SURNAME
        defaultProducerShouldNotBeFound("surname.doesNotContain=" + DEFAULT_SURNAME);

        // Get all the producerList where surname does not contain UPDATED_SURNAME
        defaultProducerShouldBeFound("surname.doesNotContain=" + UPDATED_SURNAME);
    }


    @Test
    @Transactional
    public void getAllProducersByBioIsEqualToSomething() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where bio equals to DEFAULT_BIO
        defaultProducerShouldBeFound("bio.equals=" + DEFAULT_BIO);

        // Get all the producerList where bio equals to UPDATED_BIO
        defaultProducerShouldNotBeFound("bio.equals=" + UPDATED_BIO);
    }

    @Test
    @Transactional
    public void getAllProducersByBioIsNotEqualToSomething() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where bio not equals to DEFAULT_BIO
        defaultProducerShouldNotBeFound("bio.notEquals=" + DEFAULT_BIO);

        // Get all the producerList where bio not equals to UPDATED_BIO
        defaultProducerShouldBeFound("bio.notEquals=" + UPDATED_BIO);
    }

    @Test
    @Transactional
    public void getAllProducersByBioIsInShouldWork() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where bio in DEFAULT_BIO or UPDATED_BIO
        defaultProducerShouldBeFound("bio.in=" + DEFAULT_BIO + "," + UPDATED_BIO);

        // Get all the producerList where bio equals to UPDATED_BIO
        defaultProducerShouldNotBeFound("bio.in=" + UPDATED_BIO);
    }

    @Test
    @Transactional
    public void getAllProducersByBioIsNullOrNotNull() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where bio is not null
        defaultProducerShouldBeFound("bio.specified=true");

        // Get all the producerList where bio is null
        defaultProducerShouldNotBeFound("bio.specified=false");
    }
                @Test
    @Transactional
    public void getAllProducersByBioContainsSomething() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where bio contains DEFAULT_BIO
        defaultProducerShouldBeFound("bio.contains=" + DEFAULT_BIO);

        // Get all the producerList where bio contains UPDATED_BIO
        defaultProducerShouldNotBeFound("bio.contains=" + UPDATED_BIO);
    }

    @Test
    @Transactional
    public void getAllProducersByBioNotContainsSomething() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where bio does not contain DEFAULT_BIO
        defaultProducerShouldNotBeFound("bio.doesNotContain=" + DEFAULT_BIO);

        // Get all the producerList where bio does not contain UPDATED_BIO
        defaultProducerShouldBeFound("bio.doesNotContain=" + UPDATED_BIO);
    }


    @Test
    @Transactional
    public void getAllProducersByPhotoURLIsEqualToSomething() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where photoURL equals to DEFAULT_PHOTO_URL
        defaultProducerShouldBeFound("photoURL.equals=" + DEFAULT_PHOTO_URL);

        // Get all the producerList where photoURL equals to UPDATED_PHOTO_URL
        defaultProducerShouldNotBeFound("photoURL.equals=" + UPDATED_PHOTO_URL);
    }

    @Test
    @Transactional
    public void getAllProducersByPhotoURLIsNotEqualToSomething() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where photoURL not equals to DEFAULT_PHOTO_URL
        defaultProducerShouldNotBeFound("photoURL.notEquals=" + DEFAULT_PHOTO_URL);

        // Get all the producerList where photoURL not equals to UPDATED_PHOTO_URL
        defaultProducerShouldBeFound("photoURL.notEquals=" + UPDATED_PHOTO_URL);
    }

    @Test
    @Transactional
    public void getAllProducersByPhotoURLIsInShouldWork() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where photoURL in DEFAULT_PHOTO_URL or UPDATED_PHOTO_URL
        defaultProducerShouldBeFound("photoURL.in=" + DEFAULT_PHOTO_URL + "," + UPDATED_PHOTO_URL);

        // Get all the producerList where photoURL equals to UPDATED_PHOTO_URL
        defaultProducerShouldNotBeFound("photoURL.in=" + UPDATED_PHOTO_URL);
    }

    @Test
    @Transactional
    public void getAllProducersByPhotoURLIsNullOrNotNull() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where photoURL is not null
        defaultProducerShouldBeFound("photoURL.specified=true");

        // Get all the producerList where photoURL is null
        defaultProducerShouldNotBeFound("photoURL.specified=false");
    }
                @Test
    @Transactional
    public void getAllProducersByPhotoURLContainsSomething() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where photoURL contains DEFAULT_PHOTO_URL
        defaultProducerShouldBeFound("photoURL.contains=" + DEFAULT_PHOTO_URL);

        // Get all the producerList where photoURL contains UPDATED_PHOTO_URL
        defaultProducerShouldNotBeFound("photoURL.contains=" + UPDATED_PHOTO_URL);
    }

    @Test
    @Transactional
    public void getAllProducersByPhotoURLNotContainsSomething() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList where photoURL does not contain DEFAULT_PHOTO_URL
        defaultProducerShouldNotBeFound("photoURL.doesNotContain=" + DEFAULT_PHOTO_URL);

        // Get all the producerList where photoURL does not contain UPDATED_PHOTO_URL
        defaultProducerShouldBeFound("photoURL.doesNotContain=" + UPDATED_PHOTO_URL);
    }


    @Test
    @Transactional
    public void getAllProducersByEpisodesIsEqualToSomething() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);
        Episode episodes = EpisodeResourceIT.createEntity(em);
        em.persist(episodes);
        em.flush();
        producer.addEpisodes(episodes);
        producerRepository.saveAndFlush(producer);
        Long episodesId = episodes.getId();

        // Get all the producerList where episodes equals to episodesId
        defaultProducerShouldBeFound("episodesId.equals=" + episodesId);

        // Get all the producerList where episodes equals to episodesId + 1
        defaultProducerShouldNotBeFound("episodesId.equals=" + (episodesId + 1));
    }


    @Test
    @Transactional
    public void getAllProducersByMoviesIsEqualToSomething() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);
        Movie movies = MovieResourceIT.createEntity(em);
        em.persist(movies);
        em.flush();
        producer.addMovies(movies);
        producerRepository.saveAndFlush(producer);
        Long moviesId = movies.getId();

        // Get all the producerList where movies equals to moviesId
        defaultProducerShouldBeFound("moviesId.equals=" + moviesId);

        // Get all the producerList where movies equals to moviesId + 1
        defaultProducerShouldNotBeFound("moviesId.equals=" + (moviesId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProducerShouldBeFound(String filter) throws Exception {
        restProducerMockMvc.perform(get("/api/producers?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(producer.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].surname").value(hasItem(DEFAULT_SURNAME)))
            .andExpect(jsonPath("$.[*].bio").value(hasItem(DEFAULT_BIO)))
            .andExpect(jsonPath("$.[*].photoURL").value(hasItem(DEFAULT_PHOTO_URL)));

        // Check, that the count call also returns 1
        restProducerMockMvc.perform(get("/api/producers/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProducerShouldNotBeFound(String filter) throws Exception {
        restProducerMockMvc.perform(get("/api/producers?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProducerMockMvc.perform(get("/api/producers/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingProducer() throws Exception {
        // Get the producer
        restProducerMockMvc.perform(get("/api/producers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProducer() throws Exception {
        // Initialize the database
        producerService.save(producer);

        int databaseSizeBeforeUpdate = producerRepository.findAll().size();

        // Update the producer
        Producer updatedProducer = producerRepository.findById(producer.getId()).get();
        // Disconnect from session so that the updates on updatedProducer are not directly saved in db
        em.detach(updatedProducer);
        updatedProducer
            .name(UPDATED_NAME)
            .surname(UPDATED_SURNAME)
            .bio(UPDATED_BIO)
            .photoURL(UPDATED_PHOTO_URL);

        restProducerMockMvc.perform(put("/api/producers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedProducer)))
            .andExpect(status().isOk());

        // Validate the Producer in the database
        List<Producer> producerList = producerRepository.findAll();
        assertThat(producerList).hasSize(databaseSizeBeforeUpdate);
        Producer testProducer = producerList.get(producerList.size() - 1);
        assertThat(testProducer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProducer.getSurname()).isEqualTo(UPDATED_SURNAME);
        assertThat(testProducer.getBio()).isEqualTo(UPDATED_BIO);
        assertThat(testProducer.getPhotoURL()).isEqualTo(UPDATED_PHOTO_URL);
    }

    @Test
    @Transactional
    public void updateNonExistingProducer() throws Exception {
        int databaseSizeBeforeUpdate = producerRepository.findAll().size();

        // Create the Producer

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProducerMockMvc.perform(put("/api/producers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(producer)))
            .andExpect(status().isBadRequest());

        // Validate the Producer in the database
        List<Producer> producerList = producerRepository.findAll();
        assertThat(producerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteProducer() throws Exception {
        // Initialize the database
        producerService.save(producer);

        int databaseSizeBeforeDelete = producerRepository.findAll().size();

        // Delete the producer
        restProducerMockMvc.perform(delete("/api/producers/{id}", producer.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Producer> producerList = producerRepository.findAll();
        assertThat(producerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
