package com.serhiychuk.imdb.service.impl;

import com.serhiychuk.imdb.service.MovieService;
import com.serhiychuk.imdb.domain.Movie;
import com.serhiychuk.imdb.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Movie}.
 */
@Service
@Transactional
public class MovieServiceImpl implements MovieService {

    private final Logger log = LoggerFactory.getLogger(MovieServiceImpl.class);

    private final MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    /**
     * Save a movie.
     *
     * @param movie the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Movie save(Movie movie) {
        log.debug("Request to save Movie : {}", movie);
        return movieRepository.save(movie);
    }

    /**
     * Get all the movies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Movie> findAll(Pageable pageable) {
        log.debug("Request to get all Movies");
        return movieRepository.findAll(pageable);
    }

    /**
     * Get one movie by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Movie> findOne(Long id) {
        log.debug("Request to get Movie : {}", id);
        return movieRepository.findById(id);
    }

    /**
     * Delete the movie by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Movie : {}", id);
        movieRepository.deleteById(id);
    }
}
