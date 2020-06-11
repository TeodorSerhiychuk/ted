package com.serhiychuk.imdb.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.serhiychuk.imdb.domain.Producer;
import com.serhiychuk.imdb.domain.*; // for static metamodels
import com.serhiychuk.imdb.repository.ProducerRepository;
import com.serhiychuk.imdb.service.dto.ProducerCriteria;

/**
 * Service for executing complex queries for {@link Producer} entities in the database.
 * The main input is a {@link ProducerCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Producer} or a {@link Page} of {@link Producer} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProducerQueryService extends QueryService<Producer> {

    private final Logger log = LoggerFactory.getLogger(ProducerQueryService.class);

    private final ProducerRepository producerRepository;

    public ProducerQueryService(ProducerRepository producerRepository) {
        this.producerRepository = producerRepository;
    }

    /**
     * Return a {@link List} of {@link Producer} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Producer> findByCriteria(ProducerCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Producer> specification = createSpecification(criteria);
        return producerRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Producer} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Producer> findByCriteria(ProducerCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Producer> specification = createSpecification(criteria);
        return producerRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProducerCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Producer> specification = createSpecification(criteria);
        return producerRepository.count(specification);
    }

    /**
     * Function to convert {@link ProducerCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Producer> createSpecification(ProducerCriteria criteria) {
        Specification<Producer> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Producer_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Producer_.name));
            }
            if (criteria.getSurname() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSurname(), Producer_.surname));
            }
            if (criteria.getBio() != null) {
                specification = specification.and(buildStringSpecification(criteria.getBio(), Producer_.bio));
            }
            if (criteria.getPhotoURL() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhotoURL(), Producer_.photoURL));
            }
            if (criteria.getEpisodesId() != null) {
                specification = specification.and(buildSpecification(criteria.getEpisodesId(),
                    root -> root.join(Producer_.episodes, JoinType.LEFT).get(Episode_.id)));
            }
            if (criteria.getMoviesId() != null) {
                specification = specification.and(buildSpecification(criteria.getMoviesId(),
                    root -> root.join(Producer_.movies, JoinType.LEFT).get(Movie_.id)));
            }
        }
        return specification;
    }
}
