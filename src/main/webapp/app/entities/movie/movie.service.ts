import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IMovie } from 'app/shared/model/movie.model';

type EntityResponseType = HttpResponse<IMovie>;
type EntityArrayResponseType = HttpResponse<IMovie[]>;

@Injectable({ providedIn: 'root' })
export class MovieService {
  public resourceUrl = SERVER_API_URL + 'api/movies';

  constructor(protected http: HttpClient) {}

  create(movie: IMovie): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(movie);
    return this.http
      .post<IMovie>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(movie: IMovie): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(movie);
    return this.http
      .put<IMovie>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IMovie>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IMovie[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(movie: IMovie): IMovie {
    const copy: IMovie = Object.assign({}, movie, {
      releaseDate: movie.releaseDate && movie.releaseDate.isValid() ? movie.releaseDate.toJSON() : undefined
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.releaseDate = res.body.releaseDate ? moment(res.body.releaseDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((movie: IMovie) => {
        movie.releaseDate = movie.releaseDate ? moment(movie.releaseDate) : undefined;
      });
    }
    return res;
  }
}
