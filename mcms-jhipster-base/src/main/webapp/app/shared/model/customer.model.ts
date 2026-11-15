export interface ICustomer {
  id?: number;
  name?: string;
  contactPerson?: string | null;
  email?: string | null;
  phone?: string | null;
  address?: string | null;
  deliveryPreference?: string | null;
  paymentTerms?: string | null;
  active?: boolean;
  note?: string | null;
}

export const defaultValue: Readonly<ICustomer> = {
  active: false,
};
