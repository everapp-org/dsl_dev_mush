import dayjs from 'dayjs';
import { ICustomer } from 'app/shared/model/customer.model';
import { OrderStatus } from 'app/shared/model/enumerations/order-status.model';
import { PaymentStatus } from 'app/shared/model/enumerations/payment-status.model';

export interface ISalesOrder {
  id?: number;
  orderCode?: string;
  orderDate?: dayjs.Dayjs;
  requestedDeliveryDate?: dayjs.Dayjs | null;
  actualDeliveryDate?: dayjs.Dayjs | null;
  status?: keyof typeof OrderStatus;
  totalWeight?: number | null;
  totalRevenue?: number | null;
  currency?: string;
  invoiceNumber?: string | null;
  paymentStatus?: keyof typeof PaymentStatus;
  paymentDueDate?: dayjs.Dayjs | null;
  paymentReceivedDate?: dayjs.Dayjs | null;
  shippingAddress?: string | null;
  note?: string | null;
  customer?: ICustomer;
}

export const defaultValue: Readonly<ISalesOrder> = {};
