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
 * Criteria class for the {@link com.serhiychuk.imdb.domain.Movie} entity. This class is used
 * in {@link com.serhiychuk.imdb.web.rest.MovieResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /movies?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class MovieCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter description;

    private StringFilter country;

    private ZonedDateTimeFilter releaseDate;

    private IntegerFilter income;

    private DoubleFilter rating;

    private LongFilter createdById;

    private LongFilter actorsId;

    public MovieCriteria() {
    }

    public MovieCriteria(MovieCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.country = other.country == null ? null : other.country.copy();
        this.releaseDate = other.releaseDate == null ? null : other.releaseDate.copy();
        this.income = other.income == null ? null : other.income.copy();
        this.rating = other.rating == null ? null : other.rating.copy();
        this.createdById = other.createdById == null ? null : other.createdById.copy();
        this.actorsId = other.actorsId == null ? null : other.actorsId.copy();
    }

    @Override
    public MovieCriteria copy() {
        return new MovieCriteria(this);
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

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getCountry() {
        return country;
    }

    public void setCountry(StringFilter country) {
        this.country = country;
    }

    public ZonedDateTimeFilter getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(ZonedDateTimeFilter releaseDate) {
        this.releaseDate = releaseDate;
    }

    public IntegerFilter getIncome() {
        return income;
    }

    public void setIncome(IntegerFilter income) {
        this.income = income;
    }

    public DoubleFilter getRating() {
        return rating;
    }

    public void setRating(DoubleFilter rating) {
        this.rating = rating;
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
        final MovieCriteria that = (MovieCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
            Objects.equals(country, that.country) &&
            Objects.equals(releaseDate, that.releaseDate) &&
            Objects.equals(income, that.income) &&
            Objects.equals(rating, that.rating) &&
            Objects.equals(createdById, that.createdById) &&
            Objects.equals(actorsId, that.actorsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        description,
        country,
        releaseDate,
        income,
        rating,
        createdById,
        actorsId
        );
    }

    @Override
    public String toString() {
        return "MovieCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (country != null ? "country=" + country + ", " : "") +
                (releaseDate != null ? "releaseDate=" + releaseDate + ", " : "") +
                (income != null ? "income=" + income + ", " : "") +
                (rating != null ? "rating=" + rating + ", " : "") +
                (createdById != null ? "createdById=" + createdById + ", " : "") +
                (actorsId != null ? "actorsId=" + actorsId + ", " : "") +
            "}";
    }

}
