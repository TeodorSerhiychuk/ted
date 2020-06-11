package com.serhiychuk.imdb.service;

import com.serhiychuk.imdb.domain.Role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link Role}.
 */
public interface RoleService {

    /**
     * Save a role.
     *
     * @param role the entity to save.
     * @return the persisted entity.
     */
    Role save(Role role);

    /**
     * Get all the roles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Role> findAll(Pageable pageable);

    /**
     * Get the "id" role.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Role> findOne(Long id);

    /**
     * Delete the "id" role.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
