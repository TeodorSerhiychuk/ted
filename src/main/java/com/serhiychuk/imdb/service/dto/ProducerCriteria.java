package com.serhiychuk.imdb.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.serhiychuk.imdb.domain.Producer} entity. This class is used
 * in {@link com.serhiychuk.imdb.web.rest.ProducerResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /producers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProducerCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter surname;

    private StringFilter bio;

    private StringFilter photoURL;

    private LongFilter episodesId;

    private LongFilter moviesId;

    public ProducerCriteria() {
    }

    public ProducerCriteria(ProducerCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.surname = other.surname == null ? null : other.surname.copy();
        this.bio = other.bio == null ? null : other.bio.copy();
        this.photoURL = other.photoURL == null ? null : other.photoURL.copy();
        this.episodesId = other.episodesId == null ? null : other.episodesId.copy();
        this.moviesId = other.moviesId == null ? null : other.moviesId.copy();
    }

    @Override
    public ProducerCriteria copy() {
        return new ProducerCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getSurname() {
        return surname;
    }

    public void setSurname(StringFilter surname) {
        this.surname = surname;
    }

    public StringFilter getBio() {
        return bio;
    }

    public void setBio(StringFilter bio) {
        this.bio = bio;
    }

    public StringFilter getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(StringFilter photoURL) {
        this.photoURL = photoURL;
    }

    public LongFilter getEpisodesId() {
        return episodesId;
    }

    public void setEpisodesId(LongFilter episodesId) {
        this.episodesId = episodesId;
    }

    public LongFilter getMoviesId() {
        return moviesId;
    }

    public void setMoviesId(LongFilter moviesId) {
        this.moviesId = moviesId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProducerCriteria that = (ProducerCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(surname, that.surname) &&
            Objects.equals(bio, that.bio) &&
            Objects.equals(photoURL, that.photoURL) &&
            Objects.equals(episodesId, that.episodesId) &&
            Objects.equals(moviesId, that.moviesId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        surname,
        bio,
        photoURL,
        episodesId,
        moviesId
        );
    }

    @Override
    public String toString() {
        return "ProducerCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (surname != null ? "surname=" + surname + ", " : "") +
                (bio != null ? "bio=" + bio + ", " : "") +
                (photoURL != null ? "photoURL=" + photoURL + ", " : "") +
                (episodesId != null ? "episodesId=" + episodesId + ", " : "") +
                (moviesId != null ? "moviesId=" + moviesId + ", " : "") +
            "}";
    }

}
