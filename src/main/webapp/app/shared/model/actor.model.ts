import { IRole } from 'app/shared/model/role.model';
import { IMovie } from 'app/shared/model/movie.model';
import { IEpisode } from 'app/shared/model/episode.model';

export interface IActor {
  id?: number;
  name?: string;
  surname?: string;
  bio?: string;
  photoURL?: string;
  roles?: IRole[];
  movies?: IMovie[];
  episodes?: IEpisode[];
}

export class Actor implements IActor {
  constructor(
    public id?: number,
    public name?: string,
    public surname?: string,
    public bio?: string,
    public photoURL?: string,
    public roles?: IRole[],
    public movies?: IMovie[],
    public episodes?: IEpisode[]
  ) {}
}
