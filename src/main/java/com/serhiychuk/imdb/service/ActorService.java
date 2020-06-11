package com.serhiychuk.imdb.service;

import com.serhiychuk.imdb.domain.Actor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link Actor}.
 */
public interface ActorService {

    /**
     * Save a actor.
     *
     * @param actor the entity to save.
     * @return the persisted entity.
     */
    Actor save(Actor actor);

    /**
     * Get all the actors.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Actor> findAll(Pageable pageable);

    /**
     * Get all the actors with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Page<Actor> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" actor.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Actor> findOne(Long id);

    /**
     * Delete the "id" actor.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
