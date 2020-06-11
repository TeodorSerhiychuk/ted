package com.serhiychuk.imdb.domain;


import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

/**
 * A Series.
 */
@Entity
@Table(name = "series")
public class Series implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "rating")
    private Double rating;

    @OneToMany(mappedBy = "series")
    private Set<Episode> episodes = new HashSet<>();

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

    public Series title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public Series description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRating() {
        return rating;
    }

    public Series rating(Double rating) {
        this.rating = rating;
        return this;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Set<Episode> getEpisodes() {
        return episodes;
    }

    public Series episodes(Set<Episode> episodes) {
        this.episodes = episodes;
        return this;
    }

    public Series addEpisodes(Episode episode) {
        this.episodes.add(episode);
        episode.setSeries(this);
        return this;
    }

    public Series removeEpisodes(Episode episode) {
        this.episodes.remove(episode);
        episode.setSeries(null);
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
        if (!(o instanceof Series)) {
            return false;
        }
        return id != null && id.equals(((Series) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Series{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", rating=" + getRating() +
            "}";
    }
}
