package com.serhiychuk.imdb.web.rest;

import com.serhiychuk.imdb.domain.Producer;
import com.serhiychuk.imdb.service.ProducerService;
import com.serhiychuk.imdb.web.rest.errors.BadRequestAlertException;
import com.serhiychuk.imdb.service.dto.ProducerCriteria;
import com.serhiychuk.imdb.service.ProducerQueryService;

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

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.serhiychuk.imdb.domain.Producer}.
 */
@RestController
@RequestMapping("/api")
public class ProducerResource {

    private final Logger log = LoggerFactory.getLogger(ProducerResource.class);

    private static final String ENTITY_NAME = "producer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProducerService producerService;

    private final ProducerQueryService producerQueryService;

    public ProducerResource(ProducerService producerService, ProducerQueryService producerQueryService) {
        this.producerService = producerService;
        this.producerQueryService = producerQueryService;
    }

    /**
     * {@code POST  /producers} : Create a new producer.
     *
     * @param producer the producer to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new producer, or with status {@code 400 (Bad Request)} if the producer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/producers")
    public ResponseEntity<Producer> createProducer(@Valid @RequestBody Producer producer) throws URISyntaxException {
        log.debug("REST request to save Producer : {}", producer);
        if (producer.getId() != null) {
            throw new BadRequestAlertException("A new producer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Producer result = producerService.save(producer);
        return ResponseEntity.created(new URI("/api/producers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /producers} : Updates an existing producer.
     *
     * @param producer the producer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated producer,
     * or with status {@code 400 (Bad Request)} if the producer is not valid,
     * or with status {@code 500 (Internal Server Error)} if the producer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/producers")
    public ResponseEntity<Producer> updateProducer(@Valid @RequestBody Producer producer) throws URISyntaxException {
        log.debug("REST request to update Producer : {}", producer);
        if (producer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Producer result = producerService.save(producer);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, producer.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /producers} : get all the producers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of producers in body.
     */
    @GetMapping("/producers")
    public ResponseEntity<List<Producer>> getAllProducers(ProducerCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Producers by criteria: {}", criteria);
        Page<Producer> page = producerQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /producers/count} : count all the producers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/producers/count")
    public ResponseEntity<Long> countProducers(ProducerCriteria criteria) {
        log.debug("REST request to count Producers by criteria: {}", criteria);
        return ResponseEntity.ok().body(producerQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /producers/:id} : get the "id" producer.
     *
     * @param id the id of the producer to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the producer, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/producers/{id}")
    public ResponseEntity<Producer> getProducer(@PathVariable Long id) {
        log.debug("REST request to get Producer : {}", id);
        Optional<Producer> producer = producerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(producer);
    }

    /**
     * {@code DELETE  /producers/:id} : delete the "id" producer.
     *
     * @param id the id of the producer to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/producers/{id}")
    public ResponseEntity<Void> deleteProducer(@PathVariable Long id) {
        log.debug("REST request to delete Producer : {}", id);
        producerService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
