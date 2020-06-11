import { Moment } from 'moment';
import { IProducer } from 'app/shared/model/producer.model';
import { IActor } from 'app/shared/model/actor.model';

export interface IMovie {
  id?: number;
  name?: string;
  description?: string;
  country?: string;
  releaseDate?: Moment;
  income?: number;
  rating?: number;
  createdBy?: IProducer;
  actors?: IActor[];
}

export class Movie implements IMovie {
  constructor(
    public id?: number,
    public name?: string,
    public description?: string,
    public country?: string,
    public releaseDate?: Moment,
    public income?: number,
    public rating?: number,
    public createdBy?: IProducer,
    public actors?: IActor[]
  ) {}
}
