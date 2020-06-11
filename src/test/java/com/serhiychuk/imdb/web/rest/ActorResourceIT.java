package com.serhiychuk.imdb.web.rest;

import com.serhiychuk.imdb.ImdbApp;
import com.serhiychuk.imdb.domain.Actor;
import com.serhiychuk.imdb.domain.Role;
import com.serhiychuk.imdb.domain.Movie;
import com.serhiychuk.imdb.domain.Episode;
import com.serhiychuk.imdb.repository.ActorRepository;
import com.serhiychuk.imdb.service.ActorService;
import com.serhiychuk.imdb.service.dto.ActorCriteria;
import com.serhiychuk.imdb.service.ActorQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ActorResource} REST controller.
 */
@SpringBootTest(classes = ImdbApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class ActorResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SURNAME = "AAAAAAAAAA";
    private static final String UPDATED_SURNAME = "BBBBBBBBBB";

    private static final String DEFAULT_BIO = "AAAAAAAAAA";
    private static final String UPDATED_BIO = "BBBBBBBBBB";

    private static final String DEFAULT_PHOTO_URL = "AAAAAAAAAA";
    private static final String UPDATED_PHOTO_URL = "BBBBBBBBBB";

    @Autowired
    private ActorRepository actorRepository;

    @Mock
    private ActorRepository actorRepositoryMock;

    @Mock
    private ActorService actorServiceMock;

    @Autowired
    private ActorService actorService;

    @Autowired
    private ActorQueryService actorQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restActorMockMvc;

    private Actor actor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Actor createEntity(EntityManager em) {
        Actor actor = new Actor()
            .name(DEFAULT_NAME)
            .surname(DEFAULT_SURNAME)
            .bio(DEFAULT_BIO)
            .photoURL(DEFAULT_PHOTO_URL);
        return actor;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Actor createUpdatedEntity(EntityManager em) {
        Actor actor = new Actor()
            .name(UPDATED_NAME)
            .surname(UPDATED_SURNAME)
            .bio(UPDATED_BIO)
            .photoURL(UPDATED_PHOTO_URL);
        return actor;
    }

    @BeforeEach
    public void initTest() {
        actor = createEntity(em);
    }

    @Test
    @Transactional
    public void createActor() throws Exception {
        int databaseSizeBeforeCreate = actorRepository.findAll().size();

        // Create the Actor
        restActorMockMvc.perform(post("/api/actors")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(actor)))
            .andExpect(status().isCreated());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeCreate + 1);
        Actor testActor = actorList.get(actorList.size() - 1);
        assertThat(testActor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testActor.getSurname()).isEqualTo(DEFAULT_SURNAME);
        assertThat(testActor.getBio()).isEqualTo(DEFAULT_BIO);
        assertThat(testActor.getPhotoURL()).isEqualTo(DEFAULT_PHOTO_URL);
    }

    @Test
    @Transactional
    public void createActorWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = actorRepository.findAll().size();

        // Create the Actor with an existing ID
        actor.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restActorMockMvc.perform(post("/api/actors")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(actor)))
            .andExpect(status().isBadRequest());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = actorRepository.findAll().size();
        // set the field null
        actor.setName(null);

        // Create the Actor, which fails.

        restActorMockMvc.perform(post("/api/actors")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(actor)))
            .andExpect(status().isBadRequest());

        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSurnameIsRequired() throws Exception {
        int databaseSizeBeforeTest = actorRepository.findAll().size();
        // set the field null
        actor.setSurname(null);

        // Create the Actor, which fails.

        restActorMockMvc.perform(post("/api/actors")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(actor)))
            .andExpect(status().isBadRequest());

        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllActors() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList
        restActorMockMvc.perform(get("/api/actors?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(actor.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].surname").value(hasItem(DEFAULT_SURNAME)))
            .andExpect(jsonPath("$.[*].bio").value(hasItem(DEFAULT_BIO)))
            .andExpect(jsonPath("$.[*].photoURL").value(hasItem(DEFAULT_PHOTO_URL)));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllActorsWithEagerRelationshipsIsEnabled() throws Exception {
        when(actorServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restActorMockMvc.perform(get("/api/actors?eagerload=true"))
            .andExpect(status().isOk());

        verify(actorServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllActorsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(actorServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restActorMockMvc.perform(get("/api/actors?eagerload=true"))
            .andExpect(status().isOk());

        verify(actorServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getActor() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get the actor
        restActorMockMvc.perform(get("/api/actors/{id}", actor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(actor.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.surname").value(DEFAULT_SURNAME))
            .andExpect(jsonPath("$.bio").value(DEFAULT_BIO))
            .andExpect(jsonPath("$.photoURL").value(DEFAULT_PHOTO_URL));
    }


    @Test
    @Transactional
    public void getActorsByIdFiltering() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        Long id = actor.getId();

        defaultActorShouldBeFound("id.equals=" + id);
        defaultActorShouldNotBeFound("id.notEquals=" + id);

        defaultActorShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultActorShouldNotBeFound("id.greaterThan=" + id);

        defaultActorShouldBeFound("id.lessThanOrEqual=" + id);
        defaultActorShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllActorsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where name equals to DEFAULT_NAME
        defaultActorShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the actorList where name equals to UPDATED_NAME
        defaultActorShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllActorsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where name not equals to DEFAULT_NAME
        defaultActorShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the actorList where name not equals to UPDATED_NAME
        defaultActorShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllActorsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where name in DEFAULT_NAME or UPDATED_NAME
        defaultActorShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the actorList where name equals to UPDATED_NAME
        defaultActorShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllActorsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where name is not null
        defaultActorShouldBeFound("name.specified=true");

        // Get all the actorList where name is null
        defaultActorShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllActorsByNameContainsSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where name contains DEFAULT_NAME
        defaultActorShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the actorList where name contains UPDATED_NAME
        defaultActorShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllActorsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where name does not contain DEFAULT_NAME
        defaultActorShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the actorList where name does not contain UPDATED_NAME
        defaultActorShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllActorsBySurnameIsEqualToSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where surname equals to DEFAULT_SURNAME
        defaultActorShouldBeFound("surname.equals=" + DEFAULT_SURNAME);

        // Get all the actorList where surname equals to UPDATED_SURNAME
        defaultActorShouldNotBeFound("surname.equals=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    public void getAllActorsBySurnameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where surname not equals to DEFAULT_SURNAME
        defaultActorShouldNotBeFound("surname.notEquals=" + DEFAULT_SURNAME);

        // Get all the actorList where surname not equals to UPDATED_SURNAME
        defaultActorShouldBeFound("surname.notEquals=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    public void getAllActorsBySurnameIsInShouldWork() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where surname in DEFAULT_SURNAME or UPDATED_SURNAME
        defaultActorShouldBeFound("surname.in=" + DEFAULT_SURNAME + "," + UPDATED_SURNAME);

        // Get all the actorList where surname equals to UPDATED_SURNAME
        defaultActorShouldNotBeFound("surname.in=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    public void getAllActorsBySurnameIsNullOrNotNull() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where surname is not null
        defaultActorShouldBeFound("surname.specified=true");

        // Get all the actorList where surname is null
        defaultActorShouldNotBeFound("surname.specified=false");
    }
                @Test
    @Transactional
    public void getAllActorsBySurnameContainsSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where surname contains DEFAULT_SURNAME
        defaultActorShouldBeFound("surname.contains=" + DEFAULT_SURNAME);

        // Get all the actorList where surname contains UPDATED_SURNAME
        defaultActorShouldNotBeFound("surname.contains=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    public void getAllActorsBySurnameNotContainsSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where surname does not contain DEFAULT_SURNAME
        defaultActorShouldNotBeFound("surname.doesNotContain=" + DEFAULT_SURNAME);

        // Get all the actorList where surname does not contain UPDATED_SURNAME
        defaultActorShouldBeFound("surname.doesNotContain=" + UPDATED_SURNAME);
    }


    @Test
    @Transactional
    public void getAllActorsByBioIsEqualToSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where bio equals to DEFAULT_BIO
        defaultActorShouldBeFound("bio.equals=" + DEFAULT_BIO);

        // Get all the actorList where bio equals to UPDATED_BIO
        defaultActorShouldNotBeFound("bio.equals=" + UPDATED_BIO);
    }

    @Test
    @Transactional
    public void getAllActorsByBioIsNotEqualToSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where bio not equals to DEFAULT_BIO
        defaultActorShouldNotBeFound("bio.notEquals=" + DEFAULT_BIO);

        // Get all the actorList where bio not equals to UPDATED_BIO
        defaultActorShouldBeFound("bio.notEquals=" + UPDATED_BIO);
    }

    @Test
    @Transactional
    public void getAllActorsByBioIsInShouldWork() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where bio in DEFAULT_BIO or UPDATED_BIO
        defaultActorShouldBeFound("bio.in=" + DEFAULT_BIO + "," + UPDATED_BIO);

        // Get all the actorList where bio equals to UPDATED_BIO
        defaultActorShouldNotBeFound("bio.in=" + UPDATED_BIO);
    }

    @Test
    @Transactional
    public void getAllActorsByBioIsNullOrNotNull() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where bio is not null
        defaultActorShouldBeFound("bio.specified=true");

        // Get all the actorList where bio is null
        defaultActorShouldNotBeFound("bio.specified=false");
    }
                @Test
    @Transactional
    public void getAllActorsByBioContainsSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where bio contains DEFAULT_BIO
        defaultActorShouldBeFound("bio.contains=" + DEFAULT_BIO);

        // Get all the actorList where bio contains UPDATED_BIO
        defaultActorShouldNotBeFound("bio.contains=" + UPDATED_BIO);
    }

    @Test
    @Transactional
    public void getAllActorsByBioNotContainsSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where bio does not contain DEFAULT_BIO
        defaultActorShouldNotBeFound("bio.doesNotContain=" + DEFAULT_BIO);

        // Get all the actorList where bio does not contain UPDATED_BIO
        defaultActorShouldBeFound("bio.doesNotContain=" + UPDATED_BIO);
    }


    @Test
    @Transactional
    public void getAllActorsByPhotoURLIsEqualToSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where photoURL equals to DEFAULT_PHOTO_URL
        defaultActorShouldBeFound("photoURL.equals=" + DEFAULT_PHOTO_URL);

        // Get all the actorList where photoURL equals to UPDATED_PHOTO_URL
        defaultActorShouldNotBeFound("photoURL.equals=" + UPDATED_PHOTO_URL);
    }

    @Test
    @Transactional
    public void getAllActorsByPhotoURLIsNotEqualToSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where photoURL not equals to DEFAULT_PHOTO_URL
        defaultActorShouldNotBeFound("photoURL.notEquals=" + DEFAULT_PHOTO_URL);

        // Get all the actorList where photoURL not equals to UPDATED_PHOTO_URL
        defaultActorShouldBeFound("photoURL.notEquals=" + UPDATED_PHOTO_URL);
    }

    @Test
    @Transactional
    public void getAllActorsByPhotoURLIsInShouldWork() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where photoURL in DEFAULT_PHOTO_URL or UPDATED_PHOTO_URL
        defaultActorShouldBeFound("photoURL.in=" + DEFAULT_PHOTO_URL + "," + UPDATED_PHOTO_URL);

        // Get all the actorList where photoURL equals to UPDATED_PHOTO_URL
        defaultActorShouldNotBeFound("photoURL.in=" + UPDATED_PHOTO_URL);
    }

    @Test
    @Transactional
    public void getAllActorsByPhotoURLIsNullOrNotNull() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where photoURL is not null
        defaultActorShouldBeFound("photoURL.specified=true");

        // Get all the actorList where photoURL is null
        defaultActorShouldNotBeFound("photoURL.specified=false");
    }
                @Test
    @Transactional
    public void getAllActorsByPhotoURLContainsSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where photoURL contains DEFAULT_PHOTO_URL
        defaultActorShouldBeFound("photoURL.contains=" + DEFAULT_PHOTO_URL);

        // Get all the actorList where photoURL contains UPDATED_PHOTO_URL
        defaultActorShouldNotBeFound("photoURL.contains=" + UPDATED_PHOTO_URL);
    }

    @Test
    @Transactional
    public void getAllActorsByPhotoURLNotContainsSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList where photoURL does not contain DEFAULT_PHOTO_URL
        defaultActorShouldNotBeFound("photoURL.doesNotContain=" + DEFAULT_PHOTO_URL);

        // Get all the actorList where photoURL does not contain UPDATED_PHOTO_URL
        defaultActorShouldBeFound("photoURL.doesNotContain=" + UPDATED_PHOTO_URL);
    }


    @Test
    @Transactional
    public void getAllActorsByRolesIsEqualToSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);
        Role roles = RoleResourceIT.createEntity(em);
        em.persist(roles);
        em.flush();
        actor.addRoles(roles);
        actorRepository.saveAndFlush(actor);
        Long rolesId = roles.getId();

        // Get all the actorList where roles equals to rolesId
        defaultActorShouldBeFound("rolesId.equals=" + rolesId);

        // Get all the actorList where roles equals to rolesId + 1
        defaultActorShouldNotBeFound("rolesId.equals=" + (rolesId + 1));
    }


    @Test
    @Transactional
    public void getAllActorsByMoviesIsEqualToSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);
        Movie movies = MovieResourceIT.createEntity(em);
        em.persist(movies);
        em.flush();
        actor.addMovies(movies);
        actorRepository.saveAndFlush(actor);
        Long moviesId = movies.getId();

        // Get all the actorList where movies equals to moviesId
        defaultActorShouldBeFound("moviesId.equals=" + moviesId);

        // Get all the actorList where movies equals to moviesId + 1
        defaultActorShouldNotBeFound("moviesId.equals=" + (moviesId + 1));
    }


    @Test
    @Transactional
    public void getAllActorsByEpisodesIsEqualToSomething() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);
        Episode episodes = EpisodeResourceIT.createEntity(em);
        em.persist(episodes);
        em.flush();
        actor.addEpisodes(episodes);
        actorRepository.saveAndFlush(actor);
        Long episodesId = episodes.getId();

        // Get all the actorList where episodes equals to episodesId
        defaultActorShouldBeFound("episodesId.equals=" + episodesId);

        // Get all the actorList where episodes equals to episodesId + 1
        defaultActorShouldNotBeFound("episodesId.equals=" + (episodesId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultActorShouldBeFound(String filter) throws Exception {
        restActorMockMvc.perform(get("/api/actors?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(actor.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].surname").value(hasItem(DEFAULT_SURNAME)))
            .andExpect(jsonPath("$.[*].bio").value(hasItem(DEFAULT_BIO)))
            .andExpect(jsonPath("$.[*].photoURL").value(hasItem(DEFAULT_PHOTO_URL)));

        // Check, that the count call also returns 1
        restActorMockMvc.perform(get("/api/actors/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultActorShouldNotBeFound(String filter) throws Exception {
        restActorMockMvc.perform(get("/api/actors?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restActorMockMvc.perform(get("/api/actors/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingActor() throws Exception {
        // Get the actor
        restActorMockMvc.perform(get("/api/actors/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateActor() throws Exception {
        // Initialize the database
        actorService.save(actor);

        int databaseSizeBeforeUpdate = actorRepository.findAll().size();

        // Update the actor
        Actor updatedActor = actorRepository.findById(actor.getId()).get();
        // Disconnect from session so that the updates on updatedActor are not directly saved in db
        em.detach(updatedActor);
        updatedActor
            .name(UPDATED_NAME)
            .surname(UPDATED_SURNAME)
            .bio(UPDATED_BIO)
            .photoURL(UPDATED_PHOTO_URL);

        restActorMockMvc.perform(put("/api/actors")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedActor)))
            .andExpect(status().isOk());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
        Actor testActor = actorList.get(actorList.size() - 1);
        assertThat(testActor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testActor.getSurname()).isEqualTo(UPDATED_SURNAME);
        assertThat(testActor.getBio()).isEqualTo(UPDATED_BIO);
        assertThat(testActor.getPhotoURL()).isEqualTo(UPDATED_PHOTO_URL);
    }

    @Test
    @Transactional
    public void updateNonExistingActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();

        // Create the Actor

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActorMockMvc.perform(put("/api/actors")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(actor)))
            .andExpect(status().isBadRequest());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteActor() throws Exception {
        // Initialize the database
        actorService.save(actor);

        int databaseSizeBeforeDelete = actorRepository.findAll().size();

        // Delete the actor
        restActorMockMvc.perform(delete("/api/actors/{id}", actor.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
