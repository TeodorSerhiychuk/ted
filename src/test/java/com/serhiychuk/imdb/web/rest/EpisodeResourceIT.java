package com.serhiychuk.imdb.web.rest;

import com.serhiychuk.imdb.ImdbApp;
import com.serhiychuk.imdb.domain.Episode;
import com.serhiychuk.imdb.domain.Series;
import com.serhiychuk.imdb.domain.Producer;
import com.serhiychuk.imdb.domain.Actor;
import com.serhiychuk.imdb.repository.EpisodeRepository;
import com.serhiychuk.imdb.service.EpisodeService;
import com.serhiychuk.imdb.service.dto.EpisodeCriteria;
import com.serhiychuk.imdb.service.EpisodeQueryService;

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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static com.serhiychuk.imdb.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link EpisodeResource} REST controller.
 */
@SpringBootTest(classes = ImdbApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class EpisodeResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    @Autowired
    private EpisodeRepository episodeRepository;

    @Autowired
    private EpisodeService episodeService;

    @Autowired
    private EpisodeQueryService episodeQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEpisodeMockMvc;

    private Episode episode;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Episode createEntity(EntityManager em) {
        Episode episode = new Episode()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .date(DEFAULT_DATE);
        return episode;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Episode createUpdatedEntity(EntityManager em) {
        Episode episode = new Episode()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .date(UPDATED_DATE);
        return episode;
    }

    @BeforeEach
    public void initTest() {
        episode = createEntity(em);
    }

    @Test
    @Transactional
    public void createEpisode() throws Exception {
        int databaseSizeBeforeCreate = episodeRepository.findAll().size();

        // Create the Episode
        restEpisodeMockMvc.perform(post("/api/episodes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(episode)))
            .andExpect(status().isCreated());

        // Validate the Episode in the database
        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeCreate + 1);
        Episode testEpisode = episodeList.get(episodeList.size() - 1);
        assertThat(testEpisode.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testEpisode.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEpisode.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    public void createEpisodeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = episodeRepository.findAll().size();

        // Create the Episode with an existing ID
        episode.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEpisodeMockMvc.perform(post("/api/episodes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(episode)))
            .andExpect(status().isBadRequest());

        // Validate the Episode in the database
        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllEpisodes() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList
        restEpisodeMockMvc.perform(get("/api/episodes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(episode.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))));
    }
    
    @Test
    @Transactional
    public void getEpisode() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get the episode
        restEpisodeMockMvc.perform(get("/api/episodes/{id}", episode.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(episode.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)));
    }


    @Test
    @Transactional
    public void getEpisodesByIdFiltering() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        Long id = episode.getId();

        defaultEpisodeShouldBeFound("id.equals=" + id);
        defaultEpisodeShouldNotBeFound("id.notEquals=" + id);

        defaultEpisodeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEpisodeShouldNotBeFound("id.greaterThan=" + id);

        defaultEpisodeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEpisodeShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllEpisodesByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where title equals to DEFAULT_TITLE
        defaultEpisodeShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the episodeList where title equals to UPDATED_TITLE
        defaultEpisodeShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllEpisodesByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where title not equals to DEFAULT_TITLE
        defaultEpisodeShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the episodeList where title not equals to UPDATED_TITLE
        defaultEpisodeShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllEpisodesByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultEpisodeShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the episodeList where title equals to UPDATED_TITLE
        defaultEpisodeShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllEpisodesByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where title is not null
        defaultEpisodeShouldBeFound("title.specified=true");

        // Get all the episodeList where title is null
        defaultEpisodeShouldNotBeFound("title.specified=false");
    }
                @Test
    @Transactional
    public void getAllEpisodesByTitleContainsSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where title contains DEFAULT_TITLE
        defaultEpisodeShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the episodeList where title contains UPDATED_TITLE
        defaultEpisodeShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllEpisodesByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where title does not contain DEFAULT_TITLE
        defaultEpisodeShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the episodeList where title does not contain UPDATED_TITLE
        defaultEpisodeShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }


    @Test
    @Transactional
    public void getAllEpisodesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where description equals to DEFAULT_DESCRIPTION
        defaultEpisodeShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the episodeList where description equals to UPDATED_DESCRIPTION
        defaultEpisodeShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllEpisodesByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where description not equals to DEFAULT_DESCRIPTION
        defaultEpisodeShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the episodeList where description not equals to UPDATED_DESCRIPTION
        defaultEpisodeShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllEpisodesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultEpisodeShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the episodeList where description equals to UPDATED_DESCRIPTION
        defaultEpisodeShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllEpisodesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where description is not null
        defaultEpisodeShouldBeFound("description.specified=true");

        // Get all the episodeList where description is null
        defaultEpisodeShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllEpisodesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where description contains DEFAULT_DESCRIPTION
        defaultEpisodeShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the episodeList where description contains UPDATED_DESCRIPTION
        defaultEpisodeShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllEpisodesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where description does not contain DEFAULT_DESCRIPTION
        defaultEpisodeShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the episodeList where description does not contain UPDATED_DESCRIPTION
        defaultEpisodeShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllEpisodesByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where date equals to DEFAULT_DATE
        defaultEpisodeShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the episodeList where date equals to UPDATED_DATE
        defaultEpisodeShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllEpisodesByDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where date not equals to DEFAULT_DATE
        defaultEpisodeShouldNotBeFound("date.notEquals=" + DEFAULT_DATE);

        // Get all the episodeList where date not equals to UPDATED_DATE
        defaultEpisodeShouldBeFound("date.notEquals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllEpisodesByDateIsInShouldWork() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where date in DEFAULT_DATE or UPDATED_DATE
        defaultEpisodeShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the episodeList where date equals to UPDATED_DATE
        defaultEpisodeShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllEpisodesByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where date is not null
        defaultEpisodeShouldBeFound("date.specified=true");

        // Get all the episodeList where date is null
        defaultEpisodeShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    public void getAllEpisodesByDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where date is greater than or equal to DEFAULT_DATE
        defaultEpisodeShouldBeFound("date.greaterThanOrEqual=" + DEFAULT_DATE);

        // Get all the episodeList where date is greater than or equal to UPDATED_DATE
        defaultEpisodeShouldNotBeFound("date.greaterThanOrEqual=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllEpisodesByDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where date is less than or equal to DEFAULT_DATE
        defaultEpisodeShouldBeFound("date.lessThanOrEqual=" + DEFAULT_DATE);

        // Get all the episodeList where date is less than or equal to SMALLER_DATE
        defaultEpisodeShouldNotBeFound("date.lessThanOrEqual=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    public void getAllEpisodesByDateIsLessThanSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where date is less than DEFAULT_DATE
        defaultEpisodeShouldNotBeFound("date.lessThan=" + DEFAULT_DATE);

        // Get all the episodeList where date is less than UPDATED_DATE
        defaultEpisodeShouldBeFound("date.lessThan=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllEpisodesByDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList where date is greater than DEFAULT_DATE
        defaultEpisodeShouldNotBeFound("date.greaterThan=" + DEFAULT_DATE);

        // Get all the episodeList where date is greater than SMALLER_DATE
        defaultEpisodeShouldBeFound("date.greaterThan=" + SMALLER_DATE);
    }


    @Test
    @Transactional
    public void getAllEpisodesBySeriesIsEqualToSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);
        Series series = SeriesResourceIT.createEntity(em);
        em.persist(series);
        em.flush();
        episode.setSeries(series);
        episodeRepository.saveAndFlush(episode);
        Long seriesId = series.getId();

        // Get all the episodeList where series equals to seriesId
        defaultEpisodeShouldBeFound("seriesId.equals=" + seriesId);

        // Get all the episodeList where series equals to seriesId + 1
        defaultEpisodeShouldNotBeFound("seriesId.equals=" + (seriesId + 1));
    }


    @Test
    @Transactional
    public void getAllEpisodesByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);
        Producer createdBy = ProducerResourceIT.createEntity(em);
        em.persist(createdBy);
        em.flush();
        episode.setCreatedBy(createdBy);
        episodeRepository.saveAndFlush(episode);
        Long createdById = createdBy.getId();

        // Get all the episodeList where createdBy equals to createdById
        defaultEpisodeShouldBeFound("createdById.equals=" + createdById);

        // Get all the episodeList where createdBy equals to createdById + 1
        defaultEpisodeShouldNotBeFound("createdById.equals=" + (createdById + 1));
    }


    @Test
    @Transactional
    public void getAllEpisodesByActorsIsEqualToSomething() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);
        Actor actors = ActorResourceIT.createEntity(em);
        em.persist(actors);
        em.flush();
        episode.addActors(actors);
        episodeRepository.saveAndFlush(episode);
        Long actorsId = actors.getId();

        // Get all the episodeList where actors equals to actorsId
        defaultEpisodeShouldBeFound("actorsId.equals=" + actorsId);

        // Get all the episodeList where actors equals to actorsId + 1
        defaultEpisodeShouldNotBeFound("actorsId.equals=" + (actorsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEpisodeShouldBeFound(String filter) throws Exception {
        restEpisodeMockMvc.perform(get("/api/episodes?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(episode.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))));

        // Check, that the count call also returns 1
        restEpisodeMockMvc.perform(get("/api/episodes/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEpisodeShouldNotBeFound(String filter) throws Exception {
        restEpisodeMockMvc.perform(get("/api/episodes?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEpisodeMockMvc.perform(get("/api/episodes/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingEpisode() throws Exception {
        // Get the episode
        restEpisodeMockMvc.perform(get("/api/episodes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEpisode() throws Exception {
        // Initialize the database
        episodeService.save(episode);

        int databaseSizeBeforeUpdate = episodeRepository.findAll().size();

        // Update the episode
        Episode updatedEpisode = episodeRepository.findById(episode.getId()).get();
        // Disconnect from session so that the updates on updatedEpisode are not directly saved in db
        em.detach(updatedEpisode);
        updatedEpisode
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .date(UPDATED_DATE);

        restEpisodeMockMvc.perform(put("/api/episodes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedEpisode)))
            .andExpect(status().isOk());

        // Validate the Episode in the database
        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeUpdate);
        Episode testEpisode = episodeList.get(episodeList.size() - 1);
        assertThat(testEpisode.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testEpisode.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEpisode.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingEpisode() throws Exception {
        int databaseSizeBeforeUpdate = episodeRepository.findAll().size();

        // Create the Episode

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEpisodeMockMvc.perform(put("/api/episodes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(episode)))
            .andExpect(status().isBadRequest());

        // Validate the Episode in the database
        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteEpisode() throws Exception {
        // Initialize the database
        episodeService.save(episode);

        int databaseSizeBeforeDelete = episodeRepository.findAll().size();

        // Delete the episode
        restEpisodeMockMvc.perform(delete("/api/episodes/{id}", episode.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
