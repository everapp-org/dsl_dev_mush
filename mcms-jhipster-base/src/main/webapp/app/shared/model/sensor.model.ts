import dayjs from 'dayjs';
import { IRoom } from 'app/shared/model/room.model';
import { SensorUnit } from 'app/shared/model/enumerations/sensor-unit.model';
import { SensorStatus } from 'app/shared/model/enumerations/sensor-status.model';

export interface ISensor {
  id?: number;
  sensorCode?: string;
  sensorType?: keyof typeof SensorUnit;
  status?: keyof typeof SensorStatus;
  installedDate?: dayjs.Dayjs | null;
  lastCalibrationDate?: dayjs.Dayjs | null;
  manufacturer?: string | null;
  model?: string | null;
  note?: string | null;
  room?: IRoom;
}

export const defaultValue: Readonly<ISensor> = {};
