import { MaterialCategory } from 'app/shared/model/enumerations/material-category.model';
import { UnitOfMeasure } from 'app/shared/model/enumerations/unit-of-measure.model';

export interface IMaterial {
  id?: number;
  code?: string;
  name?: string;
  category?: keyof typeof MaterialCategory;
  defaultUnit?: keyof typeof UnitOfMeasure;
  minimumStockLevel?: number | null;
  description?: string | null;
  active?: boolean;
  note?: string | null;
}

export const defaultValue: Readonly<IMaterial> = {
  active: false,
};
