package com.serhiychuk.imdb.service.impl;

import com.serhiychuk.imdb.service.SeriesService;
import com.serhiychuk.imdb.domain.Series;
import com.serhiychuk.imdb.repository.SeriesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Series}.
 */
@Service
@Transactional
public class SeriesServiceImpl implements SeriesService {

    private final Logger log = LoggerFactory.getLogger(SeriesServiceImpl.class);

    private final SeriesRepository seriesRepository;

    public SeriesServiceImpl(SeriesRepository seriesRepository) {
        this.seriesRepository = seriesRepository;
    }

    /**
     * Save a series.
     *
     * @param series the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Series save(Series series) {
        log.debug("Request to save Series : {}", series);
        return seriesRepository.save(series);
    }

    /**
     * Get all the series.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Series> findAll(Pageable pageable) {
        log.debug("Request to get all Series");
        return seriesRepository.findAll(pageable);
    }

    /**
     * Get one series by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Series> findOne(Long id) {
        log.debug("Request to get Series : {}", id);
        return seriesRepository.findById(id);
    }

    /**
     * Delete the series by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Series : {}", id);
        seriesRepository.deleteById(id);
    }
}
