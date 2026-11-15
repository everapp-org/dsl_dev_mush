import { SupplierType } from 'app/shared/model/enumerations/supplier-type.model';

export interface ISupplier {
  id?: number;
  name?: string;
  type?: keyof typeof SupplierType;
  contactPerson?: string | null;
  email?: string | null;
  phone?: string | null;
  address?: string | null;
  rating?: number | null;
  active?: boolean;
  note?: string | null;
}

export const defaultValue: Readonly<ISupplier> = {
  active: false,
};
