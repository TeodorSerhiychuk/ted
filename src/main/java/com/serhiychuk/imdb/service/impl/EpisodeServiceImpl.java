package com.serhiychuk.imdb.service.impl;

import com.serhiychuk.imdb.service.EpisodeService;
import com.serhiychuk.imdb.domain.Episode;
import com.serhiychuk.imdb.repository.EpisodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Episode}.
 */
@Service
@Transactional
public class EpisodeServiceImpl implements EpisodeService {

    private final Logger log = LoggerFactory.getLogger(EpisodeServiceImpl.class);

    private final EpisodeRepository episodeRepository;

    public EpisodeServiceImpl(EpisodeRepository episodeRepository) {
        this.episodeRepository = episodeRepository;
    }

    /**
     * Save a episode.
     *
     * @param episode the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Episode save(Episode episode) {
        log.debug("Request to save Episode : {}", episode);
        return episodeRepository.save(episode);
    }

    /**
     * Get all the episodes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Episode> findAll(Pageable pageable) {
        log.debug("Request to get all Episodes");
        return episodeRepository.findAll(pageable);
    }

    /**
     * Get one episode by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Episode> findOne(Long id) {
        log.debug("Request to get Episode : {}", id);
        return episodeRepository.findById(id);
    }

    /**
     * Delete the episode by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Episode : {}", id);
        episodeRepository.deleteById(id);
    }
}
