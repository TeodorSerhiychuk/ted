import { IEpisode } from 'app/shared/model/episode.model';
import { IMovie } from 'app/shared/model/movie.model';

export interface IProducer {
  id?: number;
  name?: string;
  surname?: string;
  bio?: string;
  photoURL?: string;
  episodes?: IEpisode[];
  movies?: IMovie[];
}

export class Producer implements IProducer {
  constructor(
    public id?: number,
    public name?: string,
    public surname?: string,
    public bio?: string,
    public photoURL?: string,
    public episodes?: IEpisode[],
    public movies?: IMovie[]
  ) {}
}
