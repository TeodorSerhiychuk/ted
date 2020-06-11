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
 * A Movie.
 */
@Entity
@Table(name = "movie")
public class Movie implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "country")
    private String country;

    @Column(name = "release_date")
    private ZonedDateTime releaseDate;

    @Column(name = "income")
    private Integer income;

    @Column(name = "rating")
    private Double rating;

    @ManyToOne
    @JsonIgnoreProperties("movies")
    private Producer createdBy;

    @ManyToMany(mappedBy = "movies")
    @JsonIgnore
    private Set<Actor> actors = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Movie name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Movie description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public Movie country(String country) {
        this.country = country;
        return this;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public ZonedDateTime getReleaseDate() {
        return releaseDate;
    }

    public Movie releaseDate(ZonedDateTime releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    public void setReleaseDate(ZonedDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Integer getIncome() {
        return income;
    }

    public Movie income(Integer income) {
        this.income = income;
        return this;
    }

    public void setIncome(Integer income) {
        this.income = income;
    }

    public Double getRating() {
        return rating;
    }

    public Movie rating(Double rating) {
        this.rating = rating;
        return this;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Producer getCreatedBy() {
        return createdBy;
    }

    public Movie createdBy(Producer producer) {
        this.createdBy = producer;
        return this;
    }

    public void setCreatedBy(Producer producer) {
        this.createdBy = producer;
    }

    public Set<Actor> getActors() {
        return actors;
    }

    public Movie actors(Set<Actor> actors) {
        this.actors = actors;
        return this;
    }

    public Movie addActors(Actor actor) {
        this.actors.add(actor);
        actor.getMovies().add(this);
        return this;
    }

    public Movie removeActors(Actor actor) {
        this.actors.remove(actor);
        actor.getMovies().remove(this);
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
        if (!(o instanceof Movie)) {
            return false;
        }
        return id != null && id.equals(((Movie) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Movie{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", country='" + getCountry() + "'" +
            ", releaseDate='" + getReleaseDate() + "'" +
            ", income=" + getIncome() +
            ", rating=" + getRating() +
            "}";
    }
}
