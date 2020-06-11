import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ISeries } from 'app/shared/model/series.model';

type EntityResponseType = HttpResponse<ISeries>;
type EntityArrayResponseType = HttpResponse<ISeries[]>;

@Injectable({ providedIn: 'root' })
export class SeriesService {
  public resourceUrl = SERVER_API_URL + 'api/series';

  constructor(protected http: HttpClient) {}

  create(series: ISeries): Observable<EntityResponseType> {
    return this.http.post<ISeries>(this.resourceUrl, series, { observe: 'response' });
  }

  update(series: ISeries): Observable<EntityResponseType> {
    return this.http.put<ISeries>(this.resourceUrl, series, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISeries>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISeries[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
