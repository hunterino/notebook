import { IUser } from 'app/shared/model/user.model';

export interface INoteBook {
  id?: number;
  name?: string;
  handle?: string;
  user?: IUser | null;
}

export const defaultValue: Readonly<INoteBook> = {};
