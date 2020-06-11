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
 * Criteria class for the {@link com.serhiychuk.imdb.domain.Series} entity. This class is used
 * in {@link com.serhiychuk.imdb.web.rest.SeriesResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /series?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class SeriesCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter description;

    private DoubleFilter rating;

    private LongFilter episodesId;

    public SeriesCriteria() {
    }

    public SeriesCriteria(SeriesCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.rating = other.rating == null ? null : other.rating.copy();
        this.episodesId = other.episodesId == null ? null : other.episodesId.copy();
    }

    @Override
    public SeriesCriteria copy() {
        return new SeriesCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTitle() {
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public DoubleFilter getRating() {
        return rating;
    }

    public void setRating(DoubleFilter rating) {
        this.rating = rating;
    }

    public LongFilter getEpisodesId() {
        return episodesId;
    }

    public void setEpisodesId(LongFilter episodesId) {
        this.episodesId = episodesId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SeriesCriteria that = (SeriesCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(description, that.description) &&
            Objects.equals(rating, that.rating) &&
            Objects.equals(episodesId, that.episodesId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        title,
        description,
        rating,
        episodesId
        );
    }

    @Override
    public String toString() {
        return "SeriesCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (title != null ? "title=" + title + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (rating != null ? "rating=" + rating + ", " : "") +
                (episodesId != null ? "episodesId=" + episodesId + ", " : "") +
            "}";
    }

}
