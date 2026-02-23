import dayjs from 'dayjs';
import { ISupplyOrder } from 'app/shared/model/supply-order.model';
import { IMaterial } from 'app/shared/model/material.model';
import { IBatch } from 'app/shared/model/batch.model';
import { UnitOfMeasure } from 'app/shared/model/enumerations/unit-of-measure.model';

export interface ISupplyOrderLine {
  id?: number;
  lineNumber?: number;
  itemDescription?: string;
  quantityOrdered?: number;
  quantityReceived?: number | null;
  unit?: keyof typeof UnitOfMeasure;
  unitPrice?: number;
  lineTotal?: number;
  lotNumber?: string | null;
  expiryDate?: dayjs.Dayjs | null;
  qualityOnReceipt?: string | null;
  isReceived?: boolean;
  note?: string | null;
  supplyOrder?: ISupplyOrder;
  material?: IMaterial;
  batch?: IBatch | null;
}

export const defaultValue: Readonly<ISupplyOrderLine> = {
  isReceived: false,
};
