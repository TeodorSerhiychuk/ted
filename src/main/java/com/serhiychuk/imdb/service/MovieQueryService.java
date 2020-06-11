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

import com.serhiychuk.imdb.domain.Movie;
import com.serhiychuk.imdb.domain.*; // for static metamodels
import com.serhiychuk.imdb.repository.MovieRepository;
import com.serhiychuk.imdb.service.dto.MovieCriteria;

/**
 * Service for executing complex queries for {@link Movie} entities in the database.
 * The main input is a {@link MovieCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Movie} or a {@link Page} of {@link Movie} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MovieQueryService extends QueryService<Movie> {

    private final Logger log = LoggerFactory.getLogger(MovieQueryService.class);

    private final MovieRepository movieRepository;

    public MovieQueryService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    /**
     * Return a {@link List} of {@link Movie} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Movie> findByCriteria(MovieCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Movie> specification = createSpecification(criteria);
        return movieRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Movie} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Movie> findByCriteria(MovieCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Movie> specification = createSpecification(criteria);
        return movieRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MovieCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Movie> specification = createSpecification(criteria);
        return movieRepository.count(specification);
    }

    /**
     * Function to convert {@link MovieCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Movie> createSpecification(MovieCriteria criteria) {
        Specification<Movie> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Movie_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Movie_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Movie_.description));
            }
            if (criteria.getCountry() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCountry(), Movie_.country));
            }
            if (criteria.getReleaseDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getReleaseDate(), Movie_.releaseDate));
            }
            if (criteria.getIncome() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getIncome(), Movie_.income));
            }
            if (criteria.getRating() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRating(), Movie_.rating));
            }
            if (criteria.getCreatedById() != null) {
                specification = specification.and(buildSpecification(criteria.getCreatedById(),
                    root -> root.join(Movie_.createdBy, JoinType.LEFT).get(Producer_.id)));
            }
            if (criteria.getActorsId() != null) {
                specification = specification.and(buildSpecification(criteria.getActorsId(),
                    root -> root.join(Movie_.actors, JoinType.LEFT).get(Actor_.id)));
            }
        }
        return specification;
    }
}
