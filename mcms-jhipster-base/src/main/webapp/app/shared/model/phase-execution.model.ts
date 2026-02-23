import dayjs from 'dayjs';
import { IBatch } from 'app/shared/model/batch.model';
import { IRoom } from 'app/shared/model/room.model';
import { PhaseName } from 'app/shared/model/enumerations/phase-name.model';

export interface IPhaseExecution {
  id?: number;
  phase?: keyof typeof PhaseName;
  sequenceOrder?: number;
  startDate?: dayjs.Dayjs;
  endDate?: dayjs.Dayjs | null;
  plannedDurationDays?: number | null;
  actualDurationDays?: number | null;
  responsiblePerson?: string | null;
  note?: string | null;
  batch?: IBatch;
  room?: IRoom | null;
}

export const defaultValue: Readonly<IPhaseExecution> = {};
