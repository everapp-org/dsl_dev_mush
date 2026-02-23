import { IStrain } from 'app/shared/model/strain.model';
import { QualityGrade } from 'app/shared/model/enumerations/quality-grade.model';

export interface IProduct {
  id?: number;
  code?: string;
  name?: string;
  description?: string | null;
  defaultGrade?: keyof typeof QualityGrade;
  defaultWeightKg?: number | null;
  defaultPricePerKg?: number | null;
  shelfLifeDays?: number | null;
  active?: boolean;
  note?: string | null;
  strain?: IStrain;
}

export const defaultValue: Readonly<IProduct> = {
  active: false,
};
