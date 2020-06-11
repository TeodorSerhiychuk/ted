package com.serhiychuk.imdb.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * A Episode.
 */
@Entity
@Table(name = "episode")
public class Episode implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "date")
    private ZonedDateTime date;

    @ManyToOne
    @JsonIgnoreProperties("episodes")
    private Series series;

    @ManyToOne
    @JsonIgnoreProperties("episodes")
    private Producer createdBy;

    @ManyToMany(mappedBy = "episodes")
    @JsonIgnore
    private Set<Actor> actors = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Episode title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public Episode description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public Episode date(ZonedDateTime date) {
        this.date = date;
        return this;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public Series getSeries() {
        return series;
    }

    public Episode series(Series series) {
        this.series = series;
        return this;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public Producer getCreatedBy() {
        return createdBy;
    }

    public Episode createdBy(Producer producer) {
        this.createdBy = producer;
        return this;
    }

    public void setCreatedBy(Producer producer) {
        this.createdBy = producer;
    }

    public Set<Actor> getActors() {
        return actors;
    }

    public Episode actors(Set<Actor> actors) {
        this.actors = actors;
        return this;
    }

    public Episode addActors(Actor actor) {
        this.actors.add(actor);
        actor.getEpisodes().add(this);
        return this;
    }

    public Episode removeActors(Actor actor) {
        this.actors.remove(actor);
        actor.getEpisodes().remove(this);
        return this;
    }

    public void setActors(Set<Actor> actors) {
        this.actors = actors;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Episode)) {
            return false;
        }
        return id != null && id.equals(((Episode) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Episode{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
