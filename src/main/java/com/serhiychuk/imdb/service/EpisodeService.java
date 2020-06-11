package com.serhiychuk.imdb.service;

import com.serhiychuk.imdb.domain.Episode;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link Episode}.
 */
public interface EpisodeService {

    /**
     * Save a episode.
     *
     * @param episode the entity to save.
     * @return the persisted entity.
     */
    Episode save(Episode episode);

    /**
     * Get all the episodes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Episode> findAll(Pageable pageable);

    /**
     * Get the "id" episode.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Episode> findOne(Long id);

    /**
     * Delete the "id" episode.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
