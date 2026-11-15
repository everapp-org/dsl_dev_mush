import dayjs from 'dayjs';
import { IBatch } from 'app/shared/model/batch.model';

export interface IFlushCycle {
  id?: number;
  flushNumber?: number;
  harvestStartDate?: dayjs.Dayjs;
  harvestEndDate?: dayjs.Dayjs | null;
  yieldKg?: number;
  yieldBagsHarvested?: number | null;
  avgFruitBodyWeightG?: number | null;
  rehydrationDone?: boolean | null;
  rehydrationDurationHours?: number | null;
  note?: string | null;
  batch?: IBatch;
}

export const defaultValue: Readonly<IFlushCycle> = {
  rehydrationDone: false,
};
