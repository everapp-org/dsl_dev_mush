import dayjs from 'dayjs';
import { IBatch } from 'app/shared/model/batch.model';
import { IInventoryLot } from 'app/shared/model/inventory-lot.model';
import { IMaterial } from 'app/shared/model/material.model';
import { UnitOfMeasure } from 'app/shared/model/enumerations/unit-of-measure.model';

export interface IBatchMaterialUsage {
  id?: number;
  usageDate?: dayjs.Dayjs;
  quantityUsed?: number;
  unit?: keyof typeof UnitOfMeasure;
  purpose?: string | null;
  note?: string | null;
  batch?: IBatch;
  inventoryLot?: IInventoryLot;
  material?: IMaterial;
}

export const defaultValue: Readonly<IBatchMaterialUsage> = {};
