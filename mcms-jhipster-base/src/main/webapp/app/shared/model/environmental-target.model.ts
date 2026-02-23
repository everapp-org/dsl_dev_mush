import { IRoom } from 'app/shared/model/room.model';
import { PhaseName } from 'app/shared/model/enumerations/phase-name.model';

export interface IEnvironmentalTarget {
  id?: number;
  phase?: keyof typeof PhaseName;
  tempMinC?: number;
  tempMaxC?: number;
  humidityMinPercent?: number;
  humidityMaxPercent?: number;
  co2MaxPpm?: number | null;
  lightLux?: number | null;
  freshAirExchangesPerHour?: number | null;
  room?: IRoom;
}

export const defaultValue: Readonly<IEnvironmentalTarget> = {};
