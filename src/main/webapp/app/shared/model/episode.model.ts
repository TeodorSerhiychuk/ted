import { Moment } from 'moment';
import { ISeries } from 'app/shared/model/series.model';
import { IProducer } from 'app/shared/model/producer.model';
import { IActor } from 'app/shared/model/actor.model';

export interface IEpisode {
  id?: number;
  title?: string;
  description?: string;
  date?: Moment;
  series?: ISeries;
  createdBy?: IProducer;
  actors?: IActor[];
}

export class Episode implements IEpisode {
  constructor(
    public id?: number,
    public title?: string,
    public description?: string,
    public date?: Moment,
    public series?: ISeries,
    public createdBy?: IProducer,
    public actors?: IActor[]
  ) {}
}
