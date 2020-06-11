package com.serhiychuk.imdb.domain;


import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

/**
 * A Producer.
 */
@Entity
@Table(name = "producer")
public class Producer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @NotNull
    @Size(max = 100)
    @Column(name = "surname", length = 100, nullable = false)
    private String surname;

    @Column(name = "bio")
    private String bio;

    @Column(name = "photo_url")
    private String photoURL;

    @OneToMany(mappedBy = "createdBy")
    private Set<Episode> episodes = new HashSet<>();

    @OneToMany(mappedBy = "createdBy")
    private Set<Movie> movies = new HashSet<>();

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

    public Producer name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public Producer surname(String surname) {
        this.surname = surname;
        return this;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBio() {
        return bio;
    }

    public Producer bio(String bio) {
        this.bio = bio;
        return this;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public Producer photoURL(String photoURL) {
        this.photoURL = photoURL;
        return this;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public Set<Episode> getEpisodes() {
        return episodes;
    }

    public Producer episodes(Set<Episode> episodes) {
        this.episodes = episodes;
        return this;
    }

    public Producer addEpisodes(Episode episode) {
        this.episodes.add(episode);
        episode.setCreatedBy(this);
        return this;
    }

    public Producer removeEpisodes(Episode episode) {
        this.episodes.remove(episode);
        episode.setCreatedBy(null);
        return this;
    }

    public void setEpisodes(Set<Episode> episodes) {
        this.episodes = episodes;
    }

    public Set<Movie> getMovies() {
        return movies;
    }

    public Producer movies(Set<Movie> movies) {
        this.movies = movies;
        return this;
    }

    public Producer addMovies(Movie movie) {
        this.movies.add(movie);
        movie.setCreatedBy(this);
        return this;
    }

    public Producer removeMovies(Movie movie) {
        this.movies.remove(movie);
        movie.setCreatedBy(null);
        return this;
    }

    public void setMovies(Set<Movie> movies) {
        this.movies = movies;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Producer)) {
            return false;
        }
        return id != null && id.equals(((Producer) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Producer{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", surname='" + getSurname() + "'" +
            ", bio='" + getBio() + "'" +
            ", photoURL='" + getPhotoURL() + "'" +
            "}";
    }
}
