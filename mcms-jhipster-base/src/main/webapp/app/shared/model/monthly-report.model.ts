import dayjs from 'dayjs';
import { IBatch } from 'app/shared/model/batch.model';

export interface IMonthlyReport {
  id?: number;
  year?: number;
  month?: number;
  generatedAt?: dayjs.Dayjs;
  totalYieldKg?: number | null;
  totalCost?: number | null;
  totalRevenue?: number | null;
  profitMarginPercent?: number | null;
  totalContaminationEvents?: number | null;
  totalMissingFields?: number | null;
  summary?: string | null;
  batch?: IBatch | null;
}

export const defaultValue: Readonly<IMonthlyReport> = {};
