import dayjs from 'dayjs';
import { IBatch } from 'app/shared/model/batch.model';
import { IRoom } from 'app/shared/model/room.model';

export interface ITask {
  id?: number;
  title?: string;
  description?: string | null;
  dueDate?: dayjs.Dayjs;
  completed?: boolean;
  completedDate?: dayjs.Dayjs | null;
  priority?: number | null;
  assignedTo?: string | null;
  note?: string | null;
  batch?: IBatch | null;
  room?: IRoom | null;
}

export const defaultValue: Readonly<ITask> = {
  completed: false,
};
