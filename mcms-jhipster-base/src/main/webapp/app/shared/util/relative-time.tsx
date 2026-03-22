/**
 * Relative Time Display Component
 * Feature #309: Show relative time (e.g., "2 hours ago") with full timestamp tooltip
 */

import React from 'react';
import { UncontrolledTooltip } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { formatRelativeTime } from 'app/config/locale-date-format';
import { APP_DATE_FORMAT, APP_TIMESTAMP_FORMAT } from 'app/config/constants';

interface RelativeTimeProps {
  /**
   * The timestamp to display (ISO 8601 string or Date)
   */
  value: string | Date | null | undefined;

  /**
   * Unique ID for the tooltip target (required for accessibility)
   */
  id: string;

  /**
   * Optional: Show only relative time without tooltip (default: false)
   */
  noTooltip?: boolean;

  /**
   * Optional: Use timestamp format with seconds (default: false)
   * When true, tooltip shows HH:mm:ss instead of HH:mm
   */
  showSeconds?: boolean;
}

/**
 * Display a timestamp as relative time (e.g., "2 hours ago") with tooltip showing full timestamp
 *
 * Usage:
 *   <RelativeTime value={alert.alertTime} id="alert-time-1" />
 *   <RelativeTime value={reading.timestamp} id="reading-time-1" showSeconds />
 *
 * Renders:
 *   "2 hours ago" with tooltip showing "03/22/26 14:30" (default)
 *   "2 hours ago" with tooltip showing "03/22/26 14:30:45" (with showSeconds)
 */
export const RelativeTime: React.FC<RelativeTimeProps> = ({ value, id, noTooltip = false, showSeconds = false }) => {
  if (!value) {
    return null;
  }

  const relativeTimeStr = formatRelativeTime(value);
  const format = showSeconds ? APP_TIMESTAMP_FORMAT : APP_DATE_FORMAT;

  if (noTooltip) {
    return <span>{relativeTimeStr}</span>;
  }

  return (
    <>
      <span id={id} style={{ cursor: 'help', textDecoration: 'underline dotted' }}>
        {relativeTimeStr}
      </span>
      <UncontrolledTooltip placement="top" target={id}>
        <TextFormat type="date" value={value} format={format} />
      </UncontrolledTooltip>
    </>
  );
};

export default RelativeTime;
