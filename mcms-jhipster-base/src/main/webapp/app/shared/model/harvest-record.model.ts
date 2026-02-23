import dayjs from 'dayjs';
import { IFlushCycle } from 'app/shared/model/flush-cycle.model';
import { QualityGrade } from 'app/shared/model/enumerations/quality-grade.model';

export interface IHarvestRecord {
  id?: number;
  harvestDate?: dayjs.Dayjs;
  weightKg?: number;
  grade?: keyof typeof QualityGrade;
  pickerName?: string | null;
  note?: string | null;
  flushCycle?: IFlushCycle;
}

export const defaultValue: Readonly<IHarvestRecord> = {};
