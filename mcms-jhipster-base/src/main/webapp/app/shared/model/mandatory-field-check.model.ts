import dayjs from 'dayjs';
import { IPhaseExecution } from 'app/shared/model/phase-execution.model';

export interface IMandatoryFieldCheck {
  id?: number;
  fieldName?: string;
  isFilled?: boolean;
  checkDate?: dayjs.Dayjs;
  phaseExecution?: IPhaseExecution | null;
}

export const defaultValue: Readonly<IMandatoryFieldCheck> = {
  isFilled: false,
};
