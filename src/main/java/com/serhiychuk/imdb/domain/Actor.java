package com.serhiychuk.imdb.domain;


import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

/**
 * A Actor.
 */
@Entity
@Table(name = "actor")
public class Actor implements Serializable {

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

    @OneToMany(mappedBy = "actor")
    private Set<Role> roles = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "actor_movies",
               joinColumns = @JoinColumn(name = "actor_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "movies_id", referencedColumnName = "id"))
    private Set<Movie> movies = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "actor_episodes",
               joinColumns = @JoinColumn(name = "actor_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "episodes_id", referencedColumnName = "id"))
    private Set<Episode> episodes = new HashSet<>();

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

    public Actor name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public Actor surname(String surname) {
        this.surname = surname;
        return this;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBio() {
        return bio;
    }

    public Actor bio(String bio) {
        this.bio = bio;
        return this;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public Actor photoURL(String photoURL) {
        this.photoURL = photoURL;
        return this;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public Actor roles(Set<Role> roles) {
        this.roles = roles;
        return this;
    }

    public Actor addRoles(Role role) {
        this.roles.add(role);
        role.setActor(this);
        return this;
    }

    public Actor removeRoles(Role role) {
        this.roles.remove(role);
        role.setActor(null);
        return this;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Movie> getMovies() {
        return movies;
    }

    public Actor movies(Set<Movie> movies) {
        this.movies = movies;
        return this;
    }

    public Actor addMovies(Movie movie) {
        this.movies.add(movie);
        movie.getActors().add(this);
        return this;
    }

    public Actor removeMovies(Movie movie) {
        this.movies.remove(movie);
        movie.getActors().remove(this);
        return this;
    }

    public void setMovies(Set<Movie> movies) {
        this.movies = movies;
    }

    public Set<Episode> getEpisodes() {
        return episodes;
    }

    public Actor episodes(Set<Episode> episodes) {
        this.episodes = episodes;
        return this;
    }

    public Actor addEpisodes(Episode episode) {
        this.episodes.add(episode);
        episode.getActors().add(this);
        return this;
    }

    public Actor removeEpisodes(Episode episode) {
        this.episodes.remove(episode);
        episode.getActors().remove(this);
        return this;
    }

    public void setEpisodes(Set<Episode> episodes) {
        this.episodes = episodes;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Actor)) {
            return false;
        }
        return id != null && id.equals(((Actor) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Actor{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", surname='" + getSurname() + "'" +
            ", bio='" + getBio() + "'" +
            ", photoURL='" + getPhotoURL() + "'" +
            "}";
    }
}
