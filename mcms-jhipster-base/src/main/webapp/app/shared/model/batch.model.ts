import dayjs from 'dayjs';
import { IStrain } from 'app/shared/model/strain.model';
import { ISubstrateRecipe } from 'app/shared/model/substrate-recipe.model';
import { PhaseName } from 'app/shared/model/enumerations/phase-name.model';

export interface IBatch {
  id?: number;
  batchCode?: string;
  startDate?: dayjs.Dayjs;
  endDate?: dayjs.Dayjs | null;
  currentPhase?: keyof typeof PhaseName;
  numberOfBags?: number | null;
  substrateWeightKg?: number | null;
  spawnWeightKg?: number | null;
  targetYieldKg?: number | null;
  actualTotalYieldKg?: number | null;
  biologicalEfficiencyPercent?: number | null;
  isContaminated?: boolean | null;
  isActive?: boolean;
  completionNote?: string | null;
  note?: string | null;
  strain?: IStrain;
  recipe?: ISubstrateRecipe;
}

export const defaultValue: Readonly<IBatch> = {
  isContaminated: false,
  isActive: false,
};
