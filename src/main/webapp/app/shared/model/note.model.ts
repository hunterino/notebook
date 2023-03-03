import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { INoteBook } from 'app/shared/model/note-book.model';

export interface INote {
  id?: number;
  title?: string;
  content?: string;
  date?: string;
  user?: IUser | null;
  notebook?: INoteBook | null;
}

export const defaultValue: Readonly<INote> = {};
