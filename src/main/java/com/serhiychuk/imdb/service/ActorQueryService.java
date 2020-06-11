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

import com.serhiychuk.imdb.domain.Actor;
import com.serhiychuk.imdb.domain.*; // for static metamodels
import com.serhiychuk.imdb.repository.ActorRepository;
import com.serhiychuk.imdb.service.dto.ActorCriteria;

/**
 * Service for executing complex queries for {@link Actor} entities in the database.
 * The main input is a {@link ActorCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Actor} or a {@link Page} of {@link Actor} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ActorQueryService extends QueryService<Actor> {

    private final Logger log = LoggerFactory.getLogger(ActorQueryService.class);

    private final ActorRepository actorRepository;

    public ActorQueryService(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    /**
     * Return a {@link List} of {@link Actor} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Actor> findByCriteria(ActorCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Actor> specification = createSpecification(criteria);
        return actorRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Actor} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Actor> findByCriteria(ActorCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Actor> specification = createSpecification(criteria);
        return actorRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ActorCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Actor> specification = createSpecification(criteria);
        return actorRepository.count(specification);
    }

    /**
     * Function to convert {@link ActorCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Actor> createSpecification(ActorCriteria criteria) {
        Specification<Actor> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Actor_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Actor_.name));
            }
            if (criteria.getSurname() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSurname(), Actor_.surname));
            }
            if (criteria.getBio() != null) {
                specification = specification.and(buildStringSpecification(criteria.getBio(), Actor_.bio));
            }
            if (criteria.getPhotoURL() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhotoURL(), Actor_.photoURL));
            }
            if (criteria.getRolesId() != null) {
                specification = specification.and(buildSpecification(criteria.getRolesId(),
                    root -> root.join(Actor_.roles, JoinType.LEFT).get(Role_.id)));
            }
            if (criteria.getMoviesId() != null) {
                specification = specification.and(buildSpecification(criteria.getMoviesId(),
                    root -> root.join(Actor_.movies, JoinType.LEFT).get(Movie_.id)));
            }
            if (criteria.getEpisodesId() != null) {
                specification = specification.and(buildSpecification(criteria.getEpisodesId(),
                    root -> root.join(Actor_.episodes, JoinType.LEFT).get(Episode_.id)));
            }
        }
        return specification;
    }
}
