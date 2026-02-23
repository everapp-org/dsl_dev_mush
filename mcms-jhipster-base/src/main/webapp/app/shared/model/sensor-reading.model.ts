import dayjs from 'dayjs';
import { ISensor } from 'app/shared/model/sensor.model';
import { SensorUnit } from 'app/shared/model/enumerations/sensor-unit.model';

export interface ISensorReading {
  id?: number;
  timestamp?: dayjs.Dayjs;
  value?: number;
  unit?: keyof typeof SensorUnit;
  sensor?: ISensor;
}

export const defaultValue: Readonly<ISensorReading> = {};
