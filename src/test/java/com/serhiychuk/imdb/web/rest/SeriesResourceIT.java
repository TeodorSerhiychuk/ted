package com.serhiychuk.imdb.web.rest;

import com.serhiychuk.imdb.ImdbApp;
import com.serhiychuk.imdb.domain.Series;
import com.serhiychuk.imdb.domain.Episode;
import com.serhiychuk.imdb.repository.SeriesRepository;
import com.serhiychuk.imdb.service.SeriesService;
import com.serhiychuk.imdb.service.dto.SeriesCriteria;
import com.serhiychuk.imdb.service.SeriesQueryService;

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
 * Integration tests for the {@link SeriesResource} REST controller.
 */
@SpringBootTest(classes = ImdbApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class SeriesResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Double DEFAULT_RATING = 1D;
    private static final Double UPDATED_RATING = 2D;
    private static final Double SMALLER_RATING = 1D - 1D;

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private SeriesService seriesService;

    @Autowired
    private SeriesQueryService seriesQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSeriesMockMvc;

    private Series series;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Series createEntity(EntityManager em) {
        Series series = new Series()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .rating(DEFAULT_RATING);
        return series;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Series createUpdatedEntity(EntityManager em) {
        Series series = new Series()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .rating(UPDATED_RATING);
        return series;
    }

    @BeforeEach
    public void initTest() {
        series = createEntity(em);
    }

    @Test
    @Transactional
    public void createSeries() throws Exception {
        int databaseSizeBeforeCreate = seriesRepository.findAll().size();

        // Create the Series
        restSeriesMockMvc.perform(post("/api/series")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(series)))
            .andExpect(status().isCreated());

        // Validate the Series in the database
        List<Series> seriesList = seriesRepository.findAll();
        assertThat(seriesList).hasSize(databaseSizeBeforeCreate + 1);
        Series testSeries = seriesList.get(seriesList.size() - 1);
        assertThat(testSeries.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSeries.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSeries.getRating()).isEqualTo(DEFAULT_RATING);
    }

    @Test
    @Transactional
    public void createSeriesWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = seriesRepository.findAll().size();

        // Create the Series with an existing ID
        series.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSeriesMockMvc.perform(post("/api/series")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(series)))
            .andExpect(status().isBadRequest());

        // Validate the Series in the database
        List<Series> seriesList = seriesRepository.findAll();
        assertThat(seriesList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllSeries() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList
        restSeriesMockMvc.perform(get("/api/series?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(series.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING.doubleValue())));
    }
    
    @Test
    @Transactional
    public void getSeries() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get the series
        restSeriesMockMvc.perform(get("/api/series/{id}", series.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(series.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING.doubleValue()));
    }


    @Test
    @Transactional
    public void getSeriesByIdFiltering() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        Long id = series.getId();

        defaultSeriesShouldBeFound("id.equals=" + id);
        defaultSeriesShouldNotBeFound("id.notEquals=" + id);

        defaultSeriesShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSeriesShouldNotBeFound("id.greaterThan=" + id);

        defaultSeriesShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSeriesShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllSeriesByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where title equals to DEFAULT_TITLE
        defaultSeriesShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the seriesList where title equals to UPDATED_TITLE
        defaultSeriesShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllSeriesByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where title not equals to DEFAULT_TITLE
        defaultSeriesShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the seriesList where title not equals to UPDATED_TITLE
        defaultSeriesShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllSeriesByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultSeriesShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the seriesList where title equals to UPDATED_TITLE
        defaultSeriesShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllSeriesByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where title is not null
        defaultSeriesShouldBeFound("title.specified=true");

        // Get all the seriesList where title is null
        defaultSeriesShouldNotBeFound("title.specified=false");
    }
                @Test
    @Transactional
    public void getAllSeriesByTitleContainsSomething() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where title contains DEFAULT_TITLE
        defaultSeriesShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the seriesList where title contains UPDATED_TITLE
        defaultSeriesShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllSeriesByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where title does not contain DEFAULT_TITLE
        defaultSeriesShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the seriesList where title does not contain UPDATED_TITLE
        defaultSeriesShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }


    @Test
    @Transactional
    public void getAllSeriesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where description equals to DEFAULT_DESCRIPTION
        defaultSeriesShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the seriesList where description equals to UPDATED_DESCRIPTION
        defaultSeriesShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllSeriesByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where description not equals to DEFAULT_DESCRIPTION
        defaultSeriesShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the seriesList where description not equals to UPDATED_DESCRIPTION
        defaultSeriesShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllSeriesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultSeriesShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the seriesList where description equals to UPDATED_DESCRIPTION
        defaultSeriesShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllSeriesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where description is not null
        defaultSeriesShouldBeFound("description.specified=true");

        // Get all the seriesList where description is null
        defaultSeriesShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllSeriesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where description contains DEFAULT_DESCRIPTION
        defaultSeriesShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the seriesList where description contains UPDATED_DESCRIPTION
        defaultSeriesShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllSeriesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where description does not contain DEFAULT_DESCRIPTION
        defaultSeriesShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the seriesList where description does not contain UPDATED_DESCRIPTION
        defaultSeriesShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllSeriesByRatingIsEqualToSomething() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where rating equals to DEFAULT_RATING
        defaultSeriesShouldBeFound("rating.equals=" + DEFAULT_RATING);

        // Get all the seriesList where rating equals to UPDATED_RATING
        defaultSeriesShouldNotBeFound("rating.equals=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    public void getAllSeriesByRatingIsNotEqualToSomething() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where rating not equals to DEFAULT_RATING
        defaultSeriesShouldNotBeFound("rating.notEquals=" + DEFAULT_RATING);

        // Get all the seriesList where rating not equals to UPDATED_RATING
        defaultSeriesShouldBeFound("rating.notEquals=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    public void getAllSeriesByRatingIsInShouldWork() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where rating in DEFAULT_RATING or UPDATED_RATING
        defaultSeriesShouldBeFound("rating.in=" + DEFAULT_RATING + "," + UPDATED_RATING);

        // Get all the seriesList where rating equals to UPDATED_RATING
        defaultSeriesShouldNotBeFound("rating.in=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    public void getAllSeriesByRatingIsNullOrNotNull() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where rating is not null
        defaultSeriesShouldBeFound("rating.specified=true");

        // Get all the seriesList where rating is null
        defaultSeriesShouldNotBeFound("rating.specified=false");
    }

    @Test
    @Transactional
    public void getAllSeriesByRatingIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where rating is greater than or equal to DEFAULT_RATING
        defaultSeriesShouldBeFound("rating.greaterThanOrEqual=" + DEFAULT_RATING);

        // Get all the seriesList where rating is greater than or equal to UPDATED_RATING
        defaultSeriesShouldNotBeFound("rating.greaterThanOrEqual=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    public void getAllSeriesByRatingIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where rating is less than or equal to DEFAULT_RATING
        defaultSeriesShouldBeFound("rating.lessThanOrEqual=" + DEFAULT_RATING);

        // Get all the seriesList where rating is less than or equal to SMALLER_RATING
        defaultSeriesShouldNotBeFound("rating.lessThanOrEqual=" + SMALLER_RATING);
    }

    @Test
    @Transactional
    public void getAllSeriesByRatingIsLessThanSomething() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where rating is less than DEFAULT_RATING
        defaultSeriesShouldNotBeFound("rating.lessThan=" + DEFAULT_RATING);

        // Get all the seriesList where rating is less than UPDATED_RATING
        defaultSeriesShouldBeFound("rating.lessThan=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    public void getAllSeriesByRatingIsGreaterThanSomething() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);

        // Get all the seriesList where rating is greater than DEFAULT_RATING
        defaultSeriesShouldNotBeFound("rating.greaterThan=" + DEFAULT_RATING);

        // Get all the seriesList where rating is greater than SMALLER_RATING
        defaultSeriesShouldBeFound("rating.greaterThan=" + SMALLER_RATING);
    }


    @Test
    @Transactional
    public void getAllSeriesByEpisodesIsEqualToSomething() throws Exception {
        // Initialize the database
        seriesRepository.saveAndFlush(series);
        Episode episodes = EpisodeResourceIT.createEntity(em);
        em.persist(episodes);
        em.flush();
        series.addEpisodes(episodes);
        seriesRepository.saveAndFlush(series);
        Long episodesId = episodes.getId();

        // Get all the seriesList where episodes equals to episodesId
        defaultSeriesShouldBeFound("episodesId.equals=" + episodesId);

        // Get all the seriesList where episodes equals to episodesId + 1
        defaultSeriesShouldNotBeFound("episodesId.equals=" + (episodesId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSeriesShouldBeFound(String filter) throws Exception {
        restSeriesMockMvc.perform(get("/api/series?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(series.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING.doubleValue())));

        // Check, that the count call also returns 1
        restSeriesMockMvc.perform(get("/api/series/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSeriesShouldNotBeFound(String filter) throws Exception {
        restSeriesMockMvc.perform(get("/api/series?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSeriesMockMvc.perform(get("/api/series/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingSeries() throws Exception {
        // Get the series
        restSeriesMockMvc.perform(get("/api/series/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSeries() throws Exception {
        // Initialize the database
        seriesService.save(series);

        int databaseSizeBeforeUpdate = seriesRepository.findAll().size();

        // Update the series
        Series updatedSeries = seriesRepository.findById(series.getId()).get();
        // Disconnect from session so that the updates on updatedSeries are not directly saved in db
        em.detach(updatedSeries);
        updatedSeries
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .rating(UPDATED_RATING);

        restSeriesMockMvc.perform(put("/api/series")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedSeries)))
            .andExpect(status().isOk());

        // Validate the Series in the database
        List<Series> seriesList = seriesRepository.findAll();
        assertThat(seriesList).hasSize(databaseSizeBeforeUpdate);
        Series testSeries = seriesList.get(seriesList.size() - 1);
        assertThat(testSeries.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSeries.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSeries.getRating()).isEqualTo(UPDATED_RATING);
    }

    @Test
    @Transactional
    public void updateNonExistingSeries() throws Exception {
        int databaseSizeBeforeUpdate = seriesRepository.findAll().size();

        // Create the Series

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSeriesMockMvc.perform(put("/api/series")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(series)))
            .andExpect(status().isBadRequest());

        // Validate the Series in the database
        List<Series> seriesList = seriesRepository.findAll();
        assertThat(seriesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteSeries() throws Exception {
        // Initialize the database
        seriesService.save(series);

        int databaseSizeBeforeDelete = seriesRepository.findAll().size();

        // Delete the series
        restSeriesMockMvc.perform(delete("/api/series/{id}", series.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Series> seriesList = seriesRepository.findAll();
        assertThat(seriesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
