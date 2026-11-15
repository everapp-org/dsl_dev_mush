import dayjs from 'dayjs';
import { IRoom } from 'app/shared/model/room.model';
import { ISensor } from 'app/shared/model/sensor.model';
import { IBatch } from 'app/shared/model/batch.model';
import { AlertSeverity } from 'app/shared/model/enumerations/alert-severity.model';
import { SensorUnit } from 'app/shared/model/enumerations/sensor-unit.model';

export interface IEnvironmentalAlert {
  id?: number;
  alertTime?: dayjs.Dayjs;
  severity?: keyof typeof AlertSeverity;
  parameter?: keyof typeof SensorUnit;
  actualValue?: number;
  thresholdValue?: number;
  message?: string;
  acknowledged?: boolean;
  acknowledgedBy?: string | null;
  acknowledgedAt?: dayjs.Dayjs | null;
  resolutionNote?: string | null;
  room?: IRoom;
  sensor?: ISensor | null;
  batch?: IBatch | null;
}

export const defaultValue: Readonly<IEnvironmentalAlert> = {
  acknowledged: false,
};
