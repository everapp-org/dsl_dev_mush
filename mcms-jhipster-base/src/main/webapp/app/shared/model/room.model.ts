import dayjs from 'dayjs';
import { RoomType } from 'app/shared/model/enumerations/room-type.model';
import { RoomStatus } from 'app/shared/model/enumerations/room-status.model';

export interface IRoom {
  id?: number;
  name?: string;
  code?: string;
  roomType?: keyof typeof RoomType;
  status?: keyof typeof RoomStatus;
  capacityBags?: number | null;
  currentOccupancy?: number | null;
  areaSqM?: number | null;
  hasHVAC?: boolean | null;
  hasMisting?: boolean | null;
  lastDisinfectionDate?: dayjs.Dayjs | null;
  note?: string | null;
}

export const defaultValue: Readonly<IRoom> = {
  hasHVAC: false,
  hasMisting: false,
};
