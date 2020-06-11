package com.serhiychuk.imdb.web.rest;

import com.serhiychuk.imdb.domain.Episode;
import com.serhiychuk.imdb.service.EpisodeService;
import com.serhiychuk.imdb.web.rest.errors.BadRequestAlertException;
import com.serhiychuk.imdb.service.dto.EpisodeCriteria;
import com.serhiychuk.imdb.service.EpisodeQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.serhiychuk.imdb.domain.Episode}.
 */
@RestController
@RequestMapping("/api")
public class EpisodeResource {

    private final Logger log = LoggerFactory.getLogger(EpisodeResource.class);

    private static final String ENTITY_NAME = "episode";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EpisodeService episodeService;

    private final EpisodeQueryService episodeQueryService;

    public EpisodeResource(EpisodeService episodeService, EpisodeQueryService episodeQueryService) {
        this.episodeService = episodeService;
        this.episodeQueryService = episodeQueryService;
    }

    /**
     * {@code POST  /episodes} : Create a new episode.
     *
     * @param episode the episode to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new episode, or with status {@code 400 (Bad Request)} if the episode has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/episodes")
    public ResponseEntity<Episode> createEpisode(@RequestBody Episode episode) throws URISyntaxException {
        log.debug("REST request to save Episode : {}", episode);
        if (episode.getId() != null) {
            throw new BadRequestAlertException("A new episode cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Episode result = episodeService.save(episode);
        return ResponseEntity.created(new URI("/api/episodes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /episodes} : Updates an existing episode.
     *
     * @param episode the episode to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated episode,
     * or with status {@code 400 (Bad Request)} if the episode is not valid,
     * or with status {@code 500 (Internal Server Error)} if the episode couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/episodes")
    public ResponseEntity<Episode> updateEpisode(@RequestBody Episode episode) throws URISyntaxException {
        log.debug("REST request to update Episode : {}", episode);
        if (episode.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Episode result = episodeService.save(episode);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, episode.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /episodes} : get all the episodes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of episodes in body.
     */
    @GetMapping("/episodes")
    public ResponseEntity<List<Episode>> getAllEpisodes(EpisodeCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Episodes by criteria: {}", criteria);
        Page<Episode> page = episodeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /episodes/count} : count all the episodes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/episodes/count")
    public ResponseEntity<Long> countEpisodes(EpisodeCriteria criteria) {
        log.debug("REST request to count Episodes by criteria: {}", criteria);
        return ResponseEntity.ok().body(episodeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /episodes/:id} : get the "id" episode.
     *
     * @param id the id of the episode to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the episode, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/episodes/{id}")
    public ResponseEntity<Episode> getEpisode(@PathVariable Long id) {
        log.debug("REST request to get Episode : {}", id);
        Optional<Episode> episode = episodeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(episode);
    }

    /**
     * {@code DELETE  /episodes/:id} : delete the "id" episode.
     *
     * @param id the id of the episode to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/episodes/{id}")
    public ResponseEntity<Void> deleteEpisode(@PathVariable Long id) {
        log.debug("REST request to delete Episode : {}", id);
        episodeService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
