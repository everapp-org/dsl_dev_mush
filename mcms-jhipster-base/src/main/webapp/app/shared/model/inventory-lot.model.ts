import dayjs from 'dayjs';
import { IMaterial } from 'app/shared/model/material.model';
import { ISupplyOrderLine } from 'app/shared/model/supply-order-line.model';
import { UnitOfMeasure } from 'app/shared/model/enumerations/unit-of-measure.model';

export interface IInventoryLot {
  id?: number;
  lotCode?: string;
  receivedDate?: dayjs.Dayjs;
  quantityReceived?: number;
  quantityOnHand?: number;
  unit?: keyof typeof UnitOfMeasure;
  expiryDate?: dayjs.Dayjs | null;
  storageLocation?: string | null;
  supplierLotNumber?: string | null;
  isExhausted?: boolean;
  note?: string | null;
  material?: IMaterial;
  supplyOrderLine?: ISupplyOrderLine;
}

export const defaultValue: Readonly<IInventoryLot> = {
  isExhausted: false,
};
