import React from 'react';
import { Badge } from 'reactstrap';

/**
 * StatusBadge Component
 *
 * Renders a color-coded badge for batch phases and status.
 * Features:
 * - Color-coded by lifecycle phase
 * - Overrides to danger/red if contaminated
 * - Bootstrap badge styling
 * - Accessible with aria-label
 *
 * Color Scheme:
 * - HARVEST: green (success) - completed batches
 * - CONTAMINATED/isContaminated: red (danger) - problematic batches
 * - INOCULATION: primary (blue) - starting phase
 * - INCUBATION: info (light blue) - growth phase
 * - COLONIZATION: info (light blue) - mycelium spread
 * - PINNING: warning (yellow) - fruit body initiation
 * - FRUITING: warning (yellow) - active fruiting
 * - HARVEST_COMPLETE: success (green) - finished harvesting
 * - DISPOSED: dark - ended batches
 * - Other phases: secondary (gray) - default
 */

interface StatusBadgeProps {
  phase: string;
  isContaminated?: boolean;
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ phase, isContaminated = false }) => {
  const getBadgeColor = (): string => {
    // Contaminated always shows as danger/red
    if (isContaminated) {
      return 'danger';
    }

    // Map phases to Bootstrap badge colors
    switch (phase) {
      case 'HARVEST':
      case 'HARVEST_COMPLETE':
        return 'success'; // green
      case 'CONTAMINATED':
      case 'DISPOSED':
        return 'danger'; // red
      case 'INOCULATION':
        return 'primary'; // blue
      case 'INCUBATION':
      case 'COLONIZATION':
        return 'info'; // light blue
      case 'PINNING':
      case 'FRUITING':
        return 'warning'; // yellow
      default:
        return 'secondary'; // gray
    }
  };

  const getAriaLabel = (): string => {
    if (isContaminated) {
      return `Phase: ${phase} (contaminated)`;
    }
    return `Phase: ${phase}`;
  };

  return (
    <Badge color={getBadgeColor()} pill aria-label={getAriaLabel()}>
      {phase}
    </Badge>
  );
};

export default StatusBadge;
