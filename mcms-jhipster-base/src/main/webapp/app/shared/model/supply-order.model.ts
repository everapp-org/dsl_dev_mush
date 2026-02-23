import dayjs from 'dayjs';
import { ISupplier } from 'app/shared/model/supplier.model';
import { OrderStatus } from 'app/shared/model/enumerations/order-status.model';

export interface ISupplyOrder {
  id?: number;
  orderCode?: string;
  orderDate?: dayjs.Dayjs;
  expectedDeliveryDate?: dayjs.Dayjs | null;
  actualDeliveryDate?: dayjs.Dayjs | null;
  status?: keyof typeof OrderStatus;
  totalAmount?: number | null;
  currency?: string;
  shippingAddress?: string | null;
  note?: string | null;
  supplier?: ISupplier;
}

export const defaultValue: Readonly<ISupplyOrder> = {};
