import { SubstrateBase } from 'app/shared/model/enumerations/substrate-base.model';

export interface ISubstrateRecipe {
  id?: number;
  name?: string;
  version?: string;
  baseType?: keyof typeof SubstrateBase;
  compositionDetail?: string | null;
  sterilizationMethod?: string | null;
  moistureTargetPercent?: number | null;
  phTarget?: number | null;
  supplementNotes?: string | null;
  active?: boolean;
}

export const defaultValue: Readonly<ISubstrateRecipe> = {
  active: false,
};
