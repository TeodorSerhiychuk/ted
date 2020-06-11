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
import io.github.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Criteria class for the {@link com.serhiychuk.imdb.domain.Episode} entity. This class is used
 * in {@link com.serhiychuk.imdb.web.rest.EpisodeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /episodes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class EpisodeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter description;

    private ZonedDateTimeFilter date;

    private LongFilter seriesId;

    private LongFilter createdById;

    private LongFilter actorsId;

    public EpisodeCriteria() {
    }

    public EpisodeCriteria(EpisodeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.seriesId = other.seriesId == null ? null : other.seriesId.copy();
        this.createdById = other.createdById == null ? null : other.createdById.copy();
        this.actorsId = other.actorsId == null ? null : other.actorsId.copy();
    }

    @Override
    public EpisodeCriteria copy() {
        return new EpisodeCriteria(this);
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

    public ZonedDateTimeFilter getDate() {
        return date;
    }

    public void setDate(ZonedDateTimeFilter date) {
        this.date = date;
    }

    public LongFilter getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(LongFilter seriesId) {
        this.seriesId = seriesId;
    }

    public LongFilter getCreatedById() {
        return createdById;
    }

    public void setCreatedById(LongFilter createdById) {
        this.createdById = createdById;
    }

    public LongFilter getActorsId() {
        return actorsId;
    }

    public void setActorsId(LongFilter actorsId) {
        this.actorsId = actorsId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EpisodeCriteria that = (EpisodeCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(description, that.description) &&
            Objects.equals(date, that.date) &&
            Objects.equals(seriesId, that.seriesId) &&
            Objects.equals(createdById, that.createdById) &&
            Objects.equals(actorsId, that.actorsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        title,
        description,
        date,
        seriesId,
        createdById,
        actorsId
        );
    }

    @Override
    public String toString() {
        return "EpisodeCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (title != null ? "title=" + title + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (date != null ? "date=" + date + ", " : "") +
                (seriesId != null ? "seriesId=" + seriesId + ", " : "") +
                (createdById != null ? "createdById=" + createdById + ", " : "") +
                (actorsId != null ? "actorsId=" + actorsId + ", " : "") +
            "}";
    }

}
