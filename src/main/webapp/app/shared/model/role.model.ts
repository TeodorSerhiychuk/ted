import { IActor } from 'app/shared/model/actor.model';

export interface IRole {
  id?: number;
  characterName?: string;
  characterDescription?: string;
  actor?: IActor;
}

export class Role implements IRole {
  constructor(public id?: number, public characterName?: string, public characterDescription?: string, public actor?: IActor) {}
}
