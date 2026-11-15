/**
 * Import locale-aware date format utilities
 * Features #307 and #308: Timezone and Locale Support
 */
import { getLocaleDateFormat, getLocaleDateTimeFormat, getLocaleTimestampFormat } from './locale-date-format';

export const AUTHORITIES = {
  ADMIN: 'ROLE_ADMIN',
  MANAGER: 'ROLE_MANAGER',
  OPERATOR: 'ROLE_OPERATOR',
  USER: 'ROLE_USER',
};

export const messages = {
  DATA_ERROR_ALERT: 'Internal Error',
};

/**
 * Date format constants - now locale-aware
 * - MM/DD/YYYY for US locales
 * - DD/MM/YYYY for other locales
 * - Timestamps (Instant fields) are automatically converted from UTC to user's local timezone by TextFormat
 */
export const APP_DATE_FORMAT = getLocaleDateTimeFormat(); // e.g., "MM/DD/YY HH:mm" or "DD/MM/YY HH:mm"
export const APP_TIMESTAMP_FORMAT = getLocaleTimestampFormat(); // e.g., "MM/DD/YY HH:mm:ss" or "DD/MM/YY HH:mm:ss"
export const APP_LOCAL_DATE_FORMAT = getLocaleDateFormat(); // e.g., "MM/DD/YYYY" or "DD/MM/YYYY"
export const APP_LOCAL_DATETIME_FORMAT = 'YYYY-MM-DDTHH:mm'; // ISO format for input fields
export const APP_WHOLE_NUMBER_FORMAT = '0,0';
export const APP_TWO_DIGITS_AFTER_POINT_NUMBER_FORMAT = '0,0.[00]';
