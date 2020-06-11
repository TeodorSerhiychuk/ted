package com.serhiychuk.imdb.service.impl;

import com.serhiychuk.imdb.service.ProducerService;
import com.serhiychuk.imdb.domain.Producer;
import com.serhiychuk.imdb.repository.ProducerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Producer}.
 */
@Service
@Transactional
public class ProducerServiceImpl implements ProducerService {

    private final Logger log = LoggerFactory.getLogger(ProducerServiceImpl.class);

    private final ProducerRepository producerRepository;

    public ProducerServiceImpl(ProducerRepository producerRepository) {
        this.producerRepository = producerRepository;
    }

    /**
     * Save a producer.
     *
     * @param producer the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Producer save(Producer producer) {
        log.debug("Request to save Producer : {}", producer);
        return producerRepository.save(producer);
    }

    /**
     * Get all the producers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Producer> findAll(Pageable pageable) {
        log.debug("Request to get all Producers");
        return producerRepository.findAll(pageable);
    }

    /**
     * Get one producer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Producer> findOne(Long id) {
        log.debug("Request to get Producer : {}", id);
        return producerRepository.findById(id);
    }

    /**
     * Delete the producer by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Producer : {}", id);
        producerRepository.deleteById(id);
    }
}
