import dayjs from 'dayjs';
import { IBatch } from 'app/shared/model/batch.model';
import { IPhaseExecution } from 'app/shared/model/phase-execution.model';
import { IRoom } from 'app/shared/model/room.model';
import { ContaminationType } from 'app/shared/model/enumerations/contamination-type.model';
import { ContaminationSeverity } from 'app/shared/model/enumerations/contamination-severity.model';
import { ContaminationAction } from 'app/shared/model/enumerations/contamination-action.model';

export interface IContaminationEvent {
  id?: number;
  detectedDate?: dayjs.Dayjs;
  type?: keyof typeof ContaminationType;
  severity?: keyof typeof ContaminationSeverity;
  affectedBags?: number | null;
  affectedPercentage?: number | null;
  actionTaken?: keyof typeof ContaminationAction;
  resolvedDate?: dayjs.Dayjs | null;
  lossKg?: number | null;
  rootCauseAnalysis?: string | null;
  preventiveMeasures?: string | null;
  detectedBy?: string | null;
  photosReference?: string | null;
  note?: string | null;
  batch?: IBatch;
  phaseExecution?: IPhaseExecution | null;
  room?: IRoom | null;
}

export const defaultValue: Readonly<IContaminationEvent> = {};
