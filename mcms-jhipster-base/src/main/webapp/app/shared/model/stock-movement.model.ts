import dayjs from 'dayjs';
import { IInventoryLot } from 'app/shared/model/inventory-lot.model';
import { IBatch } from 'app/shared/model/batch.model';
import { StockMovementType } from 'app/shared/model/enumerations/stock-movement-type.model';
import { UnitOfMeasure } from 'app/shared/model/enumerations/unit-of-measure.model';

export interface IStockMovement {
  id?: number;
  movementDate?: dayjs.Dayjs;
  movementType?: keyof typeof StockMovementType;
  quantity?: number;
  unit?: keyof typeof UnitOfMeasure;
  reference?: string | null;
  reason?: string | null;
  performedBy?: string | null;
  note?: string | null;
  inventoryLot?: IInventoryLot;
  batch?: IBatch | null;
}

export const defaultValue: Readonly<IStockMovement> = {};
