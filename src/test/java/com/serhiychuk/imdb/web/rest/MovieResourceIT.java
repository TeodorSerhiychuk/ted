package com.serhiychuk.imdb.web.rest;

import com.serhiychuk.imdb.ImdbApp;
import com.serhiychuk.imdb.domain.Movie;
import com.serhiychuk.imdb.domain.Producer;
import com.serhiychuk.imdb.domain.Actor;
import com.serhiychuk.imdb.repository.MovieRepository;
import com.serhiychuk.imdb.service.MovieService;
import com.serhiychuk.imdb.service.dto.MovieCriteria;
import com.serhiychuk.imdb.service.MovieQueryService;

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
 * Integration tests for the {@link MovieResource} REST controller.
 */
@SpringBootTest(classes = ImdbApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class MovieResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_RELEASE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_RELEASE_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_RELEASE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Integer DEFAULT_INCOME = 1;
    private static final Integer UPDATED_INCOME = 2;
    private static final Integer SMALLER_INCOME = 1 - 1;

    private static final Double DEFAULT_RATING = 1D;
    private static final Double UPDATED_RATING = 2D;
    private static final Double SMALLER_RATING = 1D - 1D;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieQueryService movieQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMovieMockMvc;

    private Movie movie;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Movie createEntity(EntityManager em) {
        Movie movie = new Movie()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .country(DEFAULT_COUNTRY)
            .releaseDate(DEFAULT_RELEASE_DATE)
            .income(DEFAULT_INCOME)
            .rating(DEFAULT_RATING);
        return movie;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Movie createUpdatedEntity(EntityManager em) {
        Movie movie = new Movie()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .country(UPDATED_COUNTRY)
            .releaseDate(UPDATED_RELEASE_DATE)
            .income(UPDATED_INCOME)
            .rating(UPDATED_RATING);
        return movie;
    }

    @BeforeEach
    public void initTest() {
        movie = createEntity(em);
    }

    @Test
    @Transactional
    public void createMovie() throws Exception {
        int databaseSizeBeforeCreate = movieRepository.findAll().size();

        // Create the Movie
        restMovieMockMvc.perform(post("/api/movies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(movie)))
            .andExpect(status().isCreated());

        // Validate the Movie in the database
        List<Movie> movieList = movieRepository.findAll();
        assertThat(movieList).hasSize(databaseSizeBeforeCreate + 1);
        Movie testMovie = movieList.get(movieList.size() - 1);
        assertThat(testMovie.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMovie.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testMovie.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testMovie.getReleaseDate()).isEqualTo(DEFAULT_RELEASE_DATE);
        assertThat(testMovie.getIncome()).isEqualTo(DEFAULT_INCOME);
        assertThat(testMovie.getRating()).isEqualTo(DEFAULT_RATING);
    }

    @Test
    @Transactional
    public void createMovieWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = movieRepository.findAll().size();

        // Create the Movie with an existing ID
        movie.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMovieMockMvc.perform(post("/api/movies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(movie)))
            .andExpect(status().isBadRequest());

        // Validate the Movie in the database
        List<Movie> movieList = movieRepository.findAll();
        assertThat(movieList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllMovies() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList
        restMovieMockMvc.perform(get("/api/movies?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(movie.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)))
            .andExpect(jsonPath("$.[*].releaseDate").value(hasItem(sameInstant(DEFAULT_RELEASE_DATE))))
            .andExpect(jsonPath("$.[*].income").value(hasItem(DEFAULT_INCOME)))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING.doubleValue())));
    }
    
    @Test
    @Transactional
    public void getMovie() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get the movie
        restMovieMockMvc.perform(get("/api/movies/{id}", movie.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(movie.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY))
            .andExpect(jsonPath("$.releaseDate").value(sameInstant(DEFAULT_RELEASE_DATE)))
            .andExpect(jsonPath("$.income").value(DEFAULT_INCOME))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING.doubleValue()));
    }


    @Test
    @Transactional
    public void getMoviesByIdFiltering() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        Long id = movie.getId();

        defaultMovieShouldBeFound("id.equals=" + id);
        defaultMovieShouldNotBeFound("id.notEquals=" + id);

        defaultMovieShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultMovieShouldNotBeFound("id.greaterThan=" + id);

        defaultMovieShouldBeFound("id.lessThanOrEqual=" + id);
        defaultMovieShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllMoviesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where name equals to DEFAULT_NAME
        defaultMovieShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the movieList where name equals to UPDATED_NAME
        defaultMovieShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllMoviesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where name not equals to DEFAULT_NAME
        defaultMovieShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the movieList where name not equals to UPDATED_NAME
        defaultMovieShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllMoviesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where name in DEFAULT_NAME or UPDATED_NAME
        defaultMovieShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the movieList where name equals to UPDATED_NAME
        defaultMovieShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllMoviesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where name is not null
        defaultMovieShouldBeFound("name.specified=true");

        // Get all the movieList where name is null
        defaultMovieShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllMoviesByNameContainsSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where name contains DEFAULT_NAME
        defaultMovieShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the movieList where name contains UPDATED_NAME
        defaultMovieShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllMoviesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where name does not contain DEFAULT_NAME
        defaultMovieShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the movieList where name does not contain UPDATED_NAME
        defaultMovieShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllMoviesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where description equals to DEFAULT_DESCRIPTION
        defaultMovieShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the movieList where description equals to UPDATED_DESCRIPTION
        defaultMovieShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllMoviesByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where description not equals to DEFAULT_DESCRIPTION
        defaultMovieShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the movieList where description not equals to UPDATED_DESCRIPTION
        defaultMovieShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllMoviesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultMovieShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the movieList where description equals to UPDATED_DESCRIPTION
        defaultMovieShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllMoviesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where description is not null
        defaultMovieShouldBeFound("description.specified=true");

        // Get all the movieList where description is null
        defaultMovieShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllMoviesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where description contains DEFAULT_DESCRIPTION
        defaultMovieShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the movieList where description contains UPDATED_DESCRIPTION
        defaultMovieShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllMoviesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where description does not contain DEFAULT_DESCRIPTION
        defaultMovieShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the movieList where description does not contain UPDATED_DESCRIPTION
        defaultMovieShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllMoviesByCountryIsEqualToSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where country equals to DEFAULT_COUNTRY
        defaultMovieShouldBeFound("country.equals=" + DEFAULT_COUNTRY);

        // Get all the movieList where country equals to UPDATED_COUNTRY
        defaultMovieShouldNotBeFound("country.equals=" + UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    public void getAllMoviesByCountryIsNotEqualToSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where country not equals to DEFAULT_COUNTRY
        defaultMovieShouldNotBeFound("country.notEquals=" + DEFAULT_COUNTRY);

        // Get all the movieList where country not equals to UPDATED_COUNTRY
        defaultMovieShouldBeFound("country.notEquals=" + UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    public void getAllMoviesByCountryIsInShouldWork() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where country in DEFAULT_COUNTRY or UPDATED_COUNTRY
        defaultMovieShouldBeFound("country.in=" + DEFAULT_COUNTRY + "," + UPDATED_COUNTRY);

        // Get all the movieList where country equals to UPDATED_COUNTRY
        defaultMovieShouldNotBeFound("country.in=" + UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    public void getAllMoviesByCountryIsNullOrNotNull() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where country is not null
        defaultMovieShouldBeFound("country.specified=true");

        // Get all the movieList where country is null
        defaultMovieShouldNotBeFound("country.specified=false");
    }
                @Test
    @Transactional
    public void getAllMoviesByCountryContainsSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where country contains DEFAULT_COUNTRY
        defaultMovieShouldBeFound("country.contains=" + DEFAULT_COUNTRY);

        // Get all the movieList where country contains UPDATED_COUNTRY
        defaultMovieShouldNotBeFound("country.contains=" + UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    public void getAllMoviesByCountryNotContainsSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where country does not contain DEFAULT_COUNTRY
        defaultMovieShouldNotBeFound("country.doesNotContain=" + DEFAULT_COUNTRY);

        // Get all the movieList where country does not contain UPDATED_COUNTRY
        defaultMovieShouldBeFound("country.doesNotContain=" + UPDATED_COUNTRY);
    }


    @Test
    @Transactional
    public void getAllMoviesByReleaseDateIsEqualToSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where releaseDate equals to DEFAULT_RELEASE_DATE
        defaultMovieShouldBeFound("releaseDate.equals=" + DEFAULT_RELEASE_DATE);

        // Get all the movieList where releaseDate equals to UPDATED_RELEASE_DATE
        defaultMovieShouldNotBeFound("releaseDate.equals=" + UPDATED_RELEASE_DATE);
    }

    @Test
    @Transactional
    public void getAllMoviesByReleaseDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where releaseDate not equals to DEFAULT_RELEASE_DATE
        defaultMovieShouldNotBeFound("releaseDate.notEquals=" + DEFAULT_RELEASE_DATE);

        // Get all the movieList where releaseDate not equals to UPDATED_RELEASE_DATE
        defaultMovieShouldBeFound("releaseDate.notEquals=" + UPDATED_RELEASE_DATE);
    }

    @Test
    @Transactional
    public void getAllMoviesByReleaseDateIsInShouldWork() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where releaseDate in DEFAULT_RELEASE_DATE or UPDATED_RELEASE_DATE
        defaultMovieShouldBeFound("releaseDate.in=" + DEFAULT_RELEASE_DATE + "," + UPDATED_RELEASE_DATE);

        // Get all the movieList where releaseDate equals to UPDATED_RELEASE_DATE
        defaultMovieShouldNotBeFound("releaseDate.in=" + UPDATED_RELEASE_DATE);
    }

    @Test
    @Transactional
    public void getAllMoviesByReleaseDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where releaseDate is not null
        defaultMovieShouldBeFound("releaseDate.specified=true");

        // Get all the movieList where releaseDate is null
        defaultMovieShouldNotBeFound("releaseDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllMoviesByReleaseDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where releaseDate is greater than or equal to DEFAULT_RELEASE_DATE
        defaultMovieShouldBeFound("releaseDate.greaterThanOrEqual=" + DEFAULT_RELEASE_DATE);

        // Get all the movieList where releaseDate is greater than or equal to UPDATED_RELEASE_DATE
        defaultMovieShouldNotBeFound("releaseDate.greaterThanOrEqual=" + UPDATED_RELEASE_DATE);
    }

    @Test
    @Transactional
    public void getAllMoviesByReleaseDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where releaseDate is less than or equal to DEFAULT_RELEASE_DATE
        defaultMovieShouldBeFound("releaseDate.lessThanOrEqual=" + DEFAULT_RELEASE_DATE);

        // Get all the movieList where releaseDate is less than or equal to SMALLER_RELEASE_DATE
        defaultMovieShouldNotBeFound("releaseDate.lessThanOrEqual=" + SMALLER_RELEASE_DATE);
    }

    @Test
    @Transactional
    public void getAllMoviesByReleaseDateIsLessThanSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where releaseDate is less than DEFAULT_RELEASE_DATE
        defaultMovieShouldNotBeFound("releaseDate.lessThan=" + DEFAULT_RELEASE_DATE);

        // Get all the movieList where releaseDate is less than UPDATED_RELEASE_DATE
        defaultMovieShouldBeFound("releaseDate.lessThan=" + UPDATED_RELEASE_DATE);
    }

    @Test
    @Transactional
    public void getAllMoviesByReleaseDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where releaseDate is greater than DEFAULT_RELEASE_DATE
        defaultMovieShouldNotBeFound("releaseDate.greaterThan=" + DEFAULT_RELEASE_DATE);

        // Get all the movieList where releaseDate is greater than SMALLER_RELEASE_DATE
        defaultMovieShouldBeFound("releaseDate.greaterThan=" + SMALLER_RELEASE_DATE);
    }


    @Test
    @Transactional
    public void getAllMoviesByIncomeIsEqualToSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where income equals to DEFAULT_INCOME
        defaultMovieShouldBeFound("income.equals=" + DEFAULT_INCOME);

        // Get all the movieList where income equals to UPDATED_INCOME
        defaultMovieShouldNotBeFound("income.equals=" + UPDATED_INCOME);
    }

    @Test
    @Transactional
    public void getAllMoviesByIncomeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where income not equals to DEFAULT_INCOME
        defaultMovieShouldNotBeFound("income.notEquals=" + DEFAULT_INCOME);

        // Get all the movieList where income not equals to UPDATED_INCOME
        defaultMovieShouldBeFound("income.notEquals=" + UPDATED_INCOME);
    }

    @Test
    @Transactional
    public void getAllMoviesByIncomeIsInShouldWork() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where income in DEFAULT_INCOME or UPDATED_INCOME
        defaultMovieShouldBeFound("income.in=" + DEFAULT_INCOME + "," + UPDATED_INCOME);

        // Get all the movieList where income equals to UPDATED_INCOME
        defaultMovieShouldNotBeFound("income.in=" + UPDATED_INCOME);
    }

    @Test
    @Transactional
    public void getAllMoviesByIncomeIsNullOrNotNull() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where income is not null
        defaultMovieShouldBeFound("income.specified=true");

        // Get all the movieList where income is null
        defaultMovieShouldNotBeFound("income.specified=false");
    }

    @Test
    @Transactional
    public void getAllMoviesByIncomeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where income is greater than or equal to DEFAULT_INCOME
        defaultMovieShouldBeFound("income.greaterThanOrEqual=" + DEFAULT_INCOME);

        // Get all the movieList where income is greater than or equal to UPDATED_INCOME
        defaultMovieShouldNotBeFound("income.greaterThanOrEqual=" + UPDATED_INCOME);
    }

    @Test
    @Transactional
    public void getAllMoviesByIncomeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where income is less than or equal to DEFAULT_INCOME
        defaultMovieShouldBeFound("income.lessThanOrEqual=" + DEFAULT_INCOME);

        // Get all the movieList where income is less than or equal to SMALLER_INCOME
        defaultMovieShouldNotBeFound("income.lessThanOrEqual=" + SMALLER_INCOME);
    }

    @Test
    @Transactional
    public void getAllMoviesByIncomeIsLessThanSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where income is less than DEFAULT_INCOME
        defaultMovieShouldNotBeFound("income.lessThan=" + DEFAULT_INCOME);

        // Get all the movieList where income is less than UPDATED_INCOME
        defaultMovieShouldBeFound("income.lessThan=" + UPDATED_INCOME);
    }

    @Test
    @Transactional
    public void getAllMoviesByIncomeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where income is greater than DEFAULT_INCOME
        defaultMovieShouldNotBeFound("income.greaterThan=" + DEFAULT_INCOME);

        // Get all the movieList where income is greater than SMALLER_INCOME
        defaultMovieShouldBeFound("income.greaterThan=" + SMALLER_INCOME);
    }


    @Test
    @Transactional
    public void getAllMoviesByRatingIsEqualToSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where rating equals to DEFAULT_RATING
        defaultMovieShouldBeFound("rating.equals=" + DEFAULT_RATING);

        // Get all the movieList where rating equals to UPDATED_RATING
        defaultMovieShouldNotBeFound("rating.equals=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    public void getAllMoviesByRatingIsNotEqualToSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where rating not equals to DEFAULT_RATING
        defaultMovieShouldNotBeFound("rating.notEquals=" + DEFAULT_RATING);

        // Get all the movieList where rating not equals to UPDATED_RATING
        defaultMovieShouldBeFound("rating.notEquals=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    public void getAllMoviesByRatingIsInShouldWork() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where rating in DEFAULT_RATING or UPDATED_RATING
        defaultMovieShouldBeFound("rating.in=" + DEFAULT_RATING + "," + UPDATED_RATING);

        // Get all the movieList where rating equals to UPDATED_RATING
        defaultMovieShouldNotBeFound("rating.in=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    public void getAllMoviesByRatingIsNullOrNotNull() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where rating is not null
        defaultMovieShouldBeFound("rating.specified=true");

        // Get all the movieList where rating is null
        defaultMovieShouldNotBeFound("rating.specified=false");
    }

    @Test
    @Transactional
    public void getAllMoviesByRatingIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where rating is greater than or equal to DEFAULT_RATING
        defaultMovieShouldBeFound("rating.greaterThanOrEqual=" + DEFAULT_RATING);

        // Get all the movieList where rating is greater than or equal to UPDATED_RATING
        defaultMovieShouldNotBeFound("rating.greaterThanOrEqual=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    public void getAllMoviesByRatingIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where rating is less than or equal to DEFAULT_RATING
        defaultMovieShouldBeFound("rating.lessThanOrEqual=" + DEFAULT_RATING);

        // Get all the movieList where rating is less than or equal to SMALLER_RATING
        defaultMovieShouldNotBeFound("rating.lessThanOrEqual=" + SMALLER_RATING);
    }

    @Test
    @Transactional
    public void getAllMoviesByRatingIsLessThanSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where rating is less than DEFAULT_RATING
        defaultMovieShouldNotBeFound("rating.lessThan=" + DEFAULT_RATING);

        // Get all the movieList where rating is less than UPDATED_RATING
        defaultMovieShouldBeFound("rating.lessThan=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    public void getAllMoviesByRatingIsGreaterThanSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList where rating is greater than DEFAULT_RATING
        defaultMovieShouldNotBeFound("rating.greaterThan=" + DEFAULT_RATING);

        // Get all the movieList where rating is greater than SMALLER_RATING
        defaultMovieShouldBeFound("rating.greaterThan=" + SMALLER_RATING);
    }


    @Test
    @Transactional
    public void getAllMoviesByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);
        Producer createdBy = ProducerResourceIT.createEntity(em);
        em.persist(createdBy);
        em.flush();
        movie.setCreatedBy(createdBy);
        movieRepository.saveAndFlush(movie);
        Long createdById = createdBy.getId();

        // Get all the movieList where createdBy equals to createdById
        defaultMovieShouldBeFound("createdById.equals=" + createdById);

        // Get all the movieList where createdBy equals to createdById + 1
        defaultMovieShouldNotBeFound("createdById.equals=" + (createdById + 1));
    }


    @Test
    @Transactional
    public void getAllMoviesByActorsIsEqualToSomething() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);
        Actor actors = ActorResourceIT.createEntity(em);
        em.persist(actors);
        em.flush();
        movie.addActors(actors);
        movieRepository.saveAndFlush(movie);
        Long actorsId = actors.getId();

        // Get all the movieList where actors equals to actorsId
        defaultMovieShouldBeFound("actorsId.equals=" + actorsId);

        // Get all the movieList where actors equals to actorsId + 1
        defaultMovieShouldNotBeFound("actorsId.equals=" + (actorsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMovieShouldBeFound(String filter) throws Exception {
        restMovieMockMvc.perform(get("/api/movies?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(movie.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)))
            .andExpect(jsonPath("$.[*].releaseDate").value(hasItem(sameInstant(DEFAULT_RELEASE_DATE))))
            .andExpect(jsonPath("$.[*].income").value(hasItem(DEFAULT_INCOME)))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING.doubleValue())));

        // Check, that the count call also returns 1
        restMovieMockMvc.perform(get("/api/movies/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMovieShouldNotBeFound(String filter) throws Exception {
        restMovieMockMvc.perform(get("/api/movies?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMovieMockMvc.perform(get("/api/movies/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingMovie() throws Exception {
        // Get the movie
        restMovieMockMvc.perform(get("/api/movies/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMovie() throws Exception {
        // Initialize the database
        movieService.save(movie);

        int databaseSizeBeforeUpdate = movieRepository.findAll().size();

        // Update the movie
        Movie updatedMovie = movieRepository.findById(movie.getId()).get();
        // Disconnect from session so that the updates on updatedMovie are not directly saved in db
        em.detach(updatedMovie);
        updatedMovie
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .country(UPDATED_COUNTRY)
            .releaseDate(UPDATED_RELEASE_DATE)
            .income(UPDATED_INCOME)
            .rating(UPDATED_RATING);

        restMovieMockMvc.perform(put("/api/movies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedMovie)))
            .andExpect(status().isOk());

        // Validate the Movie in the database
        List<Movie> movieList = movieRepository.findAll();
        assertThat(movieList).hasSize(databaseSizeBeforeUpdate);
        Movie testMovie = movieList.get(movieList.size() - 1);
        assertThat(testMovie.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMovie.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMovie.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testMovie.getReleaseDate()).isEqualTo(UPDATED_RELEASE_DATE);
        assertThat(testMovie.getIncome()).isEqualTo(UPDATED_INCOME);
        assertThat(testMovie.getRating()).isEqualTo(UPDATED_RATING);
    }

    @Test
    @Transactional
    public void updateNonExistingMovie() throws Exception {
        int databaseSizeBeforeUpdate = movieRepository.findAll().size();

        // Create the Movie

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMovieMockMvc.perform(put("/api/movies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(movie)))
            .andExpect(status().isBadRequest());

        // Validate the Movie in the database
        List<Movie> movieList = movieRepository.findAll();
        assertThat(movieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteMovie() throws Exception {
        // Initialize the database
        movieService.save(movie);

        int databaseSizeBeforeDelete = movieRepository.findAll().size();

        // Delete the movie
        restMovieMockMvc.perform(delete("/api/movies/{id}", movie.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Movie> movieList = movieRepository.findAll();
        assertThat(movieList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
