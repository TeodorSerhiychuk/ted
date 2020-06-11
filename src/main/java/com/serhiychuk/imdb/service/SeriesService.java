package com.serhiychuk.imdb.service;

import com.serhiychuk.imdb.domain.Series;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link Series}.
 */
public interface SeriesService {

    /**
     * Save a series.
     *
     * @param series the entity to save.
     * @return the persisted entity.
     */
    Series save(Series series);

    /**
     * Get all the series.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Series> findAll(Pageable pageable);

    /**
     * Get the "id" series.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Series> findOne(Long id);

    /**
     * Delete the "id" series.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
