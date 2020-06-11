package com.serhiychuk.imdb.web.rest;

import com.serhiychuk.imdb.ImdbApp;
import com.serhiychuk.imdb.domain.Role;
import com.serhiychuk.imdb.domain.Actor;
import com.serhiychuk.imdb.repository.RoleRepository;
import com.serhiychuk.imdb.service.RoleService;
import com.serhiychuk.imdb.service.dto.RoleCriteria;
import com.serhiychuk.imdb.service.RoleQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link RoleResource} REST controller.
 */
@SpringBootTest(classes = ImdbApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class RoleResourceIT {

    private static final String DEFAULT_CHARACTER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CHARACTER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CHARACTER_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_CHARACTER_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleQueryService roleQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRoleMockMvc;

    private Role role;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Role createEntity(EntityManager em) {
        Role role = new Role()
            .characterName(DEFAULT_CHARACTER_NAME)
            .characterDescription(DEFAULT_CHARACTER_DESCRIPTION);
        return role;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Role createUpdatedEntity(EntityManager em) {
        Role role = new Role()
            .characterName(UPDATED_CHARACTER_NAME)
            .characterDescription(UPDATED_CHARACTER_DESCRIPTION);
        return role;
    }

    @BeforeEach
    public void initTest() {
        role = createEntity(em);
    }

    @Test
    @Transactional
    public void createRole() throws Exception {
        int databaseSizeBeforeCreate = roleRepository.findAll().size();

        // Create the Role
        restRoleMockMvc.perform(post("/api/roles")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(role)))
            .andExpect(status().isCreated());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeCreate + 1);
        Role testRole = roleList.get(roleList.size() - 1);
        assertThat(testRole.getCharacterName()).isEqualTo(DEFAULT_CHARACTER_NAME);
        assertThat(testRole.getCharacterDescription()).isEqualTo(DEFAULT_CHARACTER_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createRoleWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = roleRepository.findAll().size();

        // Create the Role with an existing ID
        role.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRoleMockMvc.perform(post("/api/roles")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(role)))
            .andExpect(status().isBadRequest());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllRoles() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList
        restRoleMockMvc.perform(get("/api/roles?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(role.getId().intValue())))
            .andExpect(jsonPath("$.[*].characterName").value(hasItem(DEFAULT_CHARACTER_NAME)))
            .andExpect(jsonPath("$.[*].characterDescription").value(hasItem(DEFAULT_CHARACTER_DESCRIPTION)));
    }
    
    @Test
    @Transactional
    public void getRole() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get the role
        restRoleMockMvc.perform(get("/api/roles/{id}", role.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(role.getId().intValue()))
            .andExpect(jsonPath("$.characterName").value(DEFAULT_CHARACTER_NAME))
            .andExpect(jsonPath("$.characterDescription").value(DEFAULT_CHARACTER_DESCRIPTION));
    }


    @Test
    @Transactional
    public void getRolesByIdFiltering() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        Long id = role.getId();

        defaultRoleShouldBeFound("id.equals=" + id);
        defaultRoleShouldNotBeFound("id.notEquals=" + id);

        defaultRoleShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRoleShouldNotBeFound("id.greaterThan=" + id);

        defaultRoleShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRoleShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllRolesByCharacterNameIsEqualToSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where characterName equals to DEFAULT_CHARACTER_NAME
        defaultRoleShouldBeFound("characterName.equals=" + DEFAULT_CHARACTER_NAME);

        // Get all the roleList where characterName equals to UPDATED_CHARACTER_NAME
        defaultRoleShouldNotBeFound("characterName.equals=" + UPDATED_CHARACTER_NAME);
    }

    @Test
    @Transactional
    public void getAllRolesByCharacterNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where characterName not equals to DEFAULT_CHARACTER_NAME
        defaultRoleShouldNotBeFound("characterName.notEquals=" + DEFAULT_CHARACTER_NAME);

        // Get all the roleList where characterName not equals to UPDATED_CHARACTER_NAME
        defaultRoleShouldBeFound("characterName.notEquals=" + UPDATED_CHARACTER_NAME);
    }

    @Test
    @Transactional
    public void getAllRolesByCharacterNameIsInShouldWork() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where characterName in DEFAULT_CHARACTER_NAME or UPDATED_CHARACTER_NAME
        defaultRoleShouldBeFound("characterName.in=" + DEFAULT_CHARACTER_NAME + "," + UPDATED_CHARACTER_NAME);

        // Get all the roleList where characterName equals to UPDATED_CHARACTER_NAME
        defaultRoleShouldNotBeFound("characterName.in=" + UPDATED_CHARACTER_NAME);
    }

    @Test
    @Transactional
    public void getAllRolesByCharacterNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where characterName is not null
        defaultRoleShouldBeFound("characterName.specified=true");

        // Get all the roleList where characterName is null
        defaultRoleShouldNotBeFound("characterName.specified=false");
    }
                @Test
    @Transactional
    public void getAllRolesByCharacterNameContainsSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where characterName contains DEFAULT_CHARACTER_NAME
        defaultRoleShouldBeFound("characterName.contains=" + DEFAULT_CHARACTER_NAME);

        // Get all the roleList where characterName contains UPDATED_CHARACTER_NAME
        defaultRoleShouldNotBeFound("characterName.contains=" + UPDATED_CHARACTER_NAME);
    }

    @Test
    @Transactional
    public void getAllRolesByCharacterNameNotContainsSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where characterName does not contain DEFAULT_CHARACTER_NAME
        defaultRoleShouldNotBeFound("characterName.doesNotContain=" + DEFAULT_CHARACTER_NAME);

        // Get all the roleList where characterName does not contain UPDATED_CHARACTER_NAME
        defaultRoleShouldBeFound("characterName.doesNotContain=" + UPDATED_CHARACTER_NAME);
    }


    @Test
    @Transactional
    public void getAllRolesByCharacterDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where characterDescription equals to DEFAULT_CHARACTER_DESCRIPTION
        defaultRoleShouldBeFound("characterDescription.equals=" + DEFAULT_CHARACTER_DESCRIPTION);

        // Get all the roleList where characterDescription equals to UPDATED_CHARACTER_DESCRIPTION
        defaultRoleShouldNotBeFound("characterDescription.equals=" + UPDATED_CHARACTER_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllRolesByCharacterDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where characterDescription not equals to DEFAULT_CHARACTER_DESCRIPTION
        defaultRoleShouldNotBeFound("characterDescription.notEquals=" + DEFAULT_CHARACTER_DESCRIPTION);

        // Get all the roleList where characterDescription not equals to UPDATED_CHARACTER_DESCRIPTION
        defaultRoleShouldBeFound("characterDescription.notEquals=" + UPDATED_CHARACTER_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllRolesByCharacterDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where characterDescription in DEFAULT_CHARACTER_DESCRIPTION or UPDATED_CHARACTER_DESCRIPTION
        defaultRoleShouldBeFound("characterDescription.in=" + DEFAULT_CHARACTER_DESCRIPTION + "," + UPDATED_CHARACTER_DESCRIPTION);

        // Get all the roleList where characterDescription equals to UPDATED_CHARACTER_DESCRIPTION
        defaultRoleShouldNotBeFound("characterDescription.in=" + UPDATED_CHARACTER_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllRolesByCharacterDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where characterDescription is not null
        defaultRoleShouldBeFound("characterDescription.specified=true");

        // Get all the roleList where characterDescription is null
        defaultRoleShouldNotBeFound("characterDescription.specified=false");
    }
                @Test
    @Transactional
    public void getAllRolesByCharacterDescriptionContainsSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where characterDescription contains DEFAULT_CHARACTER_DESCRIPTION
        defaultRoleShouldBeFound("characterDescription.contains=" + DEFAULT_CHARACTER_DESCRIPTION);

        // Get all the roleList where characterDescription contains UPDATED_CHARACTER_DESCRIPTION
        defaultRoleShouldNotBeFound("characterDescription.contains=" + UPDATED_CHARACTER_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllRolesByCharacterDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where characterDescription does not contain DEFAULT_CHARACTER_DESCRIPTION
        defaultRoleShouldNotBeFound("characterDescription.doesNotContain=" + DEFAULT_CHARACTER_DESCRIPTION);

        // Get all the roleList where characterDescription does not contain UPDATED_CHARACTER_DESCRIPTION
        defaultRoleShouldBeFound("characterDescription.doesNotContain=" + UPDATED_CHARACTER_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllRolesByActorIsEqualToSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);
        Actor actor = ActorResourceIT.createEntity(em);
        em.persist(actor);
        em.flush();
        role.setActor(actor);
        roleRepository.saveAndFlush(role);
        Long actorId = actor.getId();

        // Get all the roleList where actor equals to actorId
        defaultRoleShouldBeFound("actorId.equals=" + actorId);

        // Get all the roleList where actor equals to actorId + 1
        defaultRoleShouldNotBeFound("actorId.equals=" + (actorId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRoleShouldBeFound(String filter) throws Exception {
        restRoleMockMvc.perform(get("/api/roles?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(role.getId().intValue())))
            .andExpect(jsonPath("$.[*].characterName").value(hasItem(DEFAULT_CHARACTER_NAME)))
            .andExpect(jsonPath("$.[*].characterDescription").value(hasItem(DEFAULT_CHARACTER_DESCRIPTION)));

        // Check, that the count call also returns 1
        restRoleMockMvc.perform(get("/api/roles/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRoleShouldNotBeFound(String filter) throws Exception {
        restRoleMockMvc.perform(get("/api/roles?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRoleMockMvc.perform(get("/api/roles/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingRole() throws Exception {
        // Get the role
        restRoleMockMvc.perform(get("/api/roles/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRole() throws Exception {
        // Initialize the database
        roleService.save(role);

        int databaseSizeBeforeUpdate = roleRepository.findAll().size();

        // Update the role
        Role updatedRole = roleRepository.findById(role.getId()).get();
        // Disconnect from session so that the updates on updatedRole are not directly saved in db
        em.detach(updatedRole);
        updatedRole
            .characterName(UPDATED_CHARACTER_NAME)
            .characterDescription(UPDATED_CHARACTER_DESCRIPTION);

        restRoleMockMvc.perform(put("/api/roles")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedRole)))
            .andExpect(status().isOk());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeUpdate);
        Role testRole = roleList.get(roleList.size() - 1);
        assertThat(testRole.getCharacterName()).isEqualTo(UPDATED_CHARACTER_NAME);
        assertThat(testRole.getCharacterDescription()).isEqualTo(UPDATED_CHARACTER_DESCRIPTION);
    }

    @Test
    @Transactional
    public void updateNonExistingRole() throws Exception {
        int databaseSizeBeforeUpdate = roleRepository.findAll().size();

        // Create the Role

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoleMockMvc.perform(put("/api/roles")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(role)))
            .andExpect(status().isBadRequest());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteRole() throws Exception {
        // Initialize the database
        roleService.save(role);

        int databaseSizeBeforeDelete = roleRepository.findAll().size();

        // Delete the role
        restRoleMockMvc.perform(delete("/api/roles/{id}", role.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
