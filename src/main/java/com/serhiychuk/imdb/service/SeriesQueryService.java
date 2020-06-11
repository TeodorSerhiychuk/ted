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

import com.serhiychuk.imdb.domain.Series;
import com.serhiychuk.imdb.domain.*; // for static metamodels
import com.serhiychuk.imdb.repository.SeriesRepository;
import com.serhiychuk.imdb.service.dto.SeriesCriteria;

/**
 * Service for executing complex queries for {@link Series} entities in the database.
 * The main input is a {@link SeriesCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Series} or a {@link Page} of {@link Series} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SeriesQueryService extends QueryService<Series> {

    private final Logger log = LoggerFactory.getLogger(SeriesQueryService.class);

    private final SeriesRepository seriesRepository;

    public SeriesQueryService(SeriesRepository seriesRepository) {
        this.seriesRepository = seriesRepository;
    }

    /**
     * Return a {@link List} of {@link Series} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Series> findByCriteria(SeriesCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Series> specification = createSpecification(criteria);
        return seriesRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Series} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Series> findByCriteria(SeriesCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Series> specification = createSpecification(criteria);
        return seriesRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SeriesCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Series> specification = createSpecification(criteria);
        return seriesRepository.count(specification);
    }

    /**
     * Function to convert {@link SeriesCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Series> createSpecification(SeriesCriteria criteria) {
        Specification<Series> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Series_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Series_.title));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Series_.description));
            }
            if (criteria.getRating() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRating(), Series_.rating));
            }
            if (criteria.getEpisodesId() != null) {
                specification = specification.and(buildSpecification(criteria.getEpisodesId(),
                    root -> root.join(Series_.episodes, JoinType.LEFT).get(Episode_.id)));
            }
        }
        return specification;
    }
}
