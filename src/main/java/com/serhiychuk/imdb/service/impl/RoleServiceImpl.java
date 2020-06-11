package com.serhiychuk.imdb.service.impl;

import com.serhiychuk.imdb.service.RoleService;
import com.serhiychuk.imdb.domain.Role;
import com.serhiychuk.imdb.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Role}.
 */
@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Save a role.
     *
     * @param role the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Role save(Role role) {
        log.debug("Request to save Role : {}", role);
        return roleRepository.save(role);
    }

    /**
     * Get all the roles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Role> findAll(Pageable pageable) {
        log.debug("Request to get all Roles");
        return roleRepository.findAll(pageable);
    }

    /**
     * Get one role by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findOne(Long id) {
        log.debug("Request to get Role : {}", id);
        return roleRepository.findById(id);
    }

    /**
     * Delete the role by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Role : {}", id);
        roleRepository.deleteById(id);
    }
}
