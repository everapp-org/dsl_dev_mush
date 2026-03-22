/**
 * Locale-aware date formatting utility
 *
 * Features #307, #308, and #309: Timezone, Locale, and Relative Time Support
 * - Timestamps are stored in UTC and displayed in user's local timezone
 * - Date formats respect user's browser locale (MM/DD/YYYY for US, DD/MM/YYYY for others)
 * - Relative time display for recent timestamps (e.g., "2 hours ago")
 */

import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import utc from 'dayjs/plugin/utc';
import timezone from 'dayjs/plugin/timezone';

// Initialize dayjs plugins
dayjs.extend(relativeTime);
dayjs.extend(utc);
dayjs.extend(timezone);

/**
 * Get the user's locale from browser
 */
export const getUserLocale = (): string => {
  return navigator.language || 'en-US';
};

/**
 * Check if user is in a US locale (uses MM/DD/YYYY format)
 */
export const isUSLocale = (): boolean => {
  const locale = getUserLocale();
  return locale.startsWith('en-US') || locale.startsWith('en_US');
};

/**
 * Get locale-appropriate date format for display
 * Returns MM/DD/YYYY for US locales, DD/MM/YYYY for others
 */
export const getLocaleDateFormat = (): string => {
  return isUSLocale() ? 'MM/DD/YYYY' : 'DD/MM/YYYY';
};

/**
 * Get locale-appropriate date-time format for display
 * Returns MM/DD/YY HH:mm for US locales, DD/MM/YY HH:mm for others
 */
export const getLocaleDateTimeFormat = (): string => {
  return isUSLocale() ? 'MM/DD/YY HH:mm' : 'DD/MM/YY HH:mm';
};

/**
 * Get locale-appropriate timestamp format for display
 * Returns MM/DD/YY HH:mm:ss for US locales, DD/MM/YY HH:mm:ss for others
 */
export const getLocaleTimestampFormat = (): string => {
  return isUSLocale() ? 'MM/DD/YY HH:mm:ss' : 'DD/MM/YY HH:mm:ss';
};

/**
 * Format a date string/Date object to locale-appropriate format
 * This is used as a fallback for when TextFormat isn't available
 */
export const formatDateToLocale = (date: Date | string): string => {
  const dateObj = typeof date === 'string' ? new Date(date) : date;
  const locale = getUserLocale();

  return new Intl.DateTimeFormat(locale, {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }).format(dateObj);
};

/**
 * Format a timestamp (Instant/DateTime) to locale-appropriate format with timezone
 * Automatically converts UTC to user's local timezone
 */
export const formatTimestampToLocale = (timestamp: Date | string): string => {
  const dateObj = typeof timestamp === 'string' ? new Date(timestamp) : timestamp;
  const locale = getUserLocale();

  return new Intl.DateTimeFormat(locale, {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false,
  }).format(dateObj);
};

/**
 * Format a timestamp as relative time (e.g., "2 hours ago", "3 days ago")
 * Feature #309: Relative time display
 *
 * @param timestamp - ISO 8601 timestamp string or Date object
 * @returns Relative time string (e.g., "2 hours ago", "in 3 days")
 */
export const formatRelativeTime = (timestamp: Date | string): string => {
  return dayjs(timestamp).fromNow();
};

/**
 * Check if a timestamp is recent (within the last 7 days)
 * Used to determine whether to show relative time vs absolute time
 *
 * @param timestamp - ISO 8601 timestamp string or Date object
 * @returns true if within 7 days, false otherwise
 */
export const isRecentTimestamp = (timestamp: Date | string, daysThreshold = 7): boolean => {
  const now = dayjs();
  const then = dayjs(timestamp);
  return now.diff(then, 'day') <= daysThreshold;
};
