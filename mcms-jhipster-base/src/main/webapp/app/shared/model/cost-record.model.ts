import dayjs from 'dayjs';
import { IBatch } from 'app/shared/model/batch.model';
import { CostCategory } from 'app/shared/model/enumerations/cost-category.model';

export interface ICostRecord {
  id?: number;
  recordDate?: dayjs.Dayjs;
  category?: keyof typeof CostCategory;
  description?: string;
  amount?: number;
  currency?: string;
  note?: string | null;
  batch?: IBatch;
}

export const defaultValue: Readonly<ICostRecord> = {};
