import { ISalesOrder } from 'app/shared/model/sales-order.model';
import { IProduct } from 'app/shared/model/product.model';
import { IBatch } from 'app/shared/model/batch.model';
import { QualityGrade } from 'app/shared/model/enumerations/quality-grade.model';
import { UnitOfMeasure } from 'app/shared/model/enumerations/unit-of-measure.model';

export interface ISalesOrderLine {
  id?: number;
  lineNumber?: number;
  weightKg?: number;
  grade?: keyof typeof QualityGrade;
  quantityUnits?: number | null;
  unit?: keyof typeof UnitOfMeasure | null;
  pricePerKg?: number;
  lineTotal?: number;
  note?: string | null;
  salesOrder?: ISalesOrder;
  product?: IProduct;
  batch?: IBatch | null;
}

export const defaultValue: Readonly<ISalesOrderLine> = {};
