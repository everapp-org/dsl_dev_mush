export interface IStrain {
  id?: number;
  name?: string;
  species?: string;
  variety?: string | null;
  optimalTempMinC?: number | null;
  optimalTempMaxC?: number | null;
  optimalHumidityMin?: number | null;
  optimalHumidityMax?: number | null;
  optimalCO2MaxPpm?: number | null;
  colonizationDaysMin?: number | null;
  colonizationDaysMax?: number | null;
  expectedYieldPercent?: number | null;
  shelfLifeDays?: number | null;
  note?: string | null;
  active?: boolean;
}

export const defaultValue: Readonly<IStrain> = {
  active: false,
};
