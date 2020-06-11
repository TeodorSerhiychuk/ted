package com.serhiychuk.imdb.service.impl;

import com.serhiychuk.imdb.service.ActorService;
import com.serhiychuk.imdb.domain.Actor;
import com.serhiychuk.imdb.repository.ActorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Actor}.
 */
@Service
@Transactional
public class ActorServiceImpl implements ActorService {

    private final Logger log = LoggerFactory.getLogger(ActorServiceImpl.class);

    private final ActorRepository actorRepository;

    public ActorServiceImpl(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    /**
     * Save a actor.
     *
     * @param actor the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Actor save(Actor actor) {
        log.debug("Request to save Actor : {}", actor);
        return actorRepository.save(actor);
    }

    /**
     * Get all the actors.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Actor> findAll(Pageable pageable) {
        log.debug("Request to get all Actors");
        return actorRepository.findAll(pageable);
    }

    /**
     * Get all the actors with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Actor> findAllWithEagerRelationships(Pageable pageable) {
        return actorRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one actor by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Actor> findOne(Long id) {
        log.debug("Request to get Actor : {}", id);
        return actorRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the actor by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Actor : {}", id);
        actorRepository.deleteById(id);
    }
}
