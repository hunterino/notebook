import { IUser } from 'app/shared/model/user.model';
import { INote } from 'app/shared/model/note.model';

export interface IShare {
  id?: number;
  invite?: string;
  author?: IUser | null;
  withUser?: IUser | null;
  sharing?: INote | null;
}

export const defaultValue: Readonly<IShare> = {};
