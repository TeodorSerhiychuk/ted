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

import com.serhiychuk.imdb.domain.Role;
import com.serhiychuk.imdb.domain.*; // for static metamodels
import com.serhiychuk.imdb.repository.RoleRepository;
import com.serhiychuk.imdb.service.dto.RoleCriteria;

/**
 * Service for executing complex queries for {@link Role} entities in the database.
 * The main input is a {@link RoleCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Role} or a {@link Page} of {@link Role} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RoleQueryService extends QueryService<Role> {

    private final Logger log = LoggerFactory.getLogger(RoleQueryService.class);

    private final RoleRepository roleRepository;

    public RoleQueryService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Return a {@link List} of {@link Role} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Role> findByCriteria(RoleCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Role> specification = createSpecification(criteria);
        return roleRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Role} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Role> findByCriteria(RoleCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Role> specification = createSpecification(criteria);
        return roleRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RoleCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Role> specification = createSpecification(criteria);
        return roleRepository.count(specification);
    }

    /**
     * Function to convert {@link RoleCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Role> createSpecification(RoleCriteria criteria) {
        Specification<Role> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Role_.id));
            }
            if (criteria.getCharacterName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCharacterName(), Role_.characterName));
            }
            if (criteria.getCharacterDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCharacterDescription(), Role_.characterDescription));
            }
            if (criteria.getActorId() != null) {
                specification = specification.and(buildSpecification(criteria.getActorId(),
                    root -> root.join(Role_.actor, JoinType.LEFT).get(Actor_.id)));
            }
        }
        return specification;
    }
}
