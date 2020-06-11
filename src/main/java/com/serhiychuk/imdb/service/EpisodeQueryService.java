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

import com.serhiychuk.imdb.domain.Episode;
import com.serhiychuk.imdb.domain.*; // for static metamodels
import com.serhiychuk.imdb.repository.EpisodeRepository;
import com.serhiychuk.imdb.service.dto.EpisodeCriteria;

/**
 * Service for executing complex queries for {@link Episode} entities in the database.
 * The main input is a {@link EpisodeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Episode} or a {@link Page} of {@link Episode} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EpisodeQueryService extends QueryService<Episode> {

    private final Logger log = LoggerFactory.getLogger(EpisodeQueryService.class);

    private final EpisodeRepository episodeRepository;

    public EpisodeQueryService(EpisodeRepository episodeRepository) {
        this.episodeRepository = episodeRepository;
    }

    /**
     * Return a {@link List} of {@link Episode} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Episode> findByCriteria(EpisodeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Episode> specification = createSpecification(criteria);
        return episodeRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Episode} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Episode> findByCriteria(EpisodeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Episode> specification = createSpecification(criteria);
        return episodeRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EpisodeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Episode> specification = createSpecification(criteria);
        return episodeRepository.count(specification);
    }

    /**
     * Function to convert {@link EpisodeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Episode> createSpecification(EpisodeCriteria criteria) {
        Specification<Episode> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Episode_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Episode_.title));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Episode_.description));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), Episode_.date));
            }
            if (criteria.getSeriesId() != null) {
                specification = specification.and(buildSpecification(criteria.getSeriesId(),
                    root -> root.join(Episode_.series, JoinType.LEFT).get(Series_.id)));
            }
            if (criteria.getCreatedById() != null) {
                specification = specification.and(buildSpecification(criteria.getCreatedById(),
                    root -> root.join(Episode_.createdBy, JoinType.LEFT).get(Producer_.id)));
            }
            if (criteria.getActorsId() != null) {
                specification = specification.and(buildSpecification(criteria.getActorsId(),
                    root -> root.join(Episode_.actors, JoinType.LEFT).get(Actor_.id)));
            }
        }
        return specification;
    }
}
