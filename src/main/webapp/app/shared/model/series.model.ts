import { IEpisode } from 'app/shared/model/episode.model';

export interface ISeries {
  id?: number;
  title?: string;
  description?: string;
  rating?: number;
  episodes?: IEpisode[];
}

export class Series implements ISeries {
  constructor(
    public id?: number,
    public title?: string,
    public description?: string,
    public rating?: number,
    public episodes?: IEpisode[]
  ) {}
}
