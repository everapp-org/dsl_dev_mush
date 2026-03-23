import React from 'react';
import { Badge } from 'reactstrap';

/**
 * StatusBadge Component
 *
 * Renders a color-coded badge for batch phases and status.
 * Features:
 * - 10 distinct colors for 10 batch lifecycle phases
 * - Overrides to danger/red if contaminated
 * - Bootstrap badge styling with custom phase colors
 * - Accessible with aria-label
 *
 * Color Scheme (10 Distinct Phases):
 * - INOCULATION: primary (blue #0d6efd) - starting phase
 * - EARLY_COLONIZATION: info (cyan #17a2b8) - mycelium spreading
 * - FULL_COLONIZATION: success (green #28a745) - colonization complete
 * - CONSOLIDATION: dark (dark gray #343a40) - strengthening
 * - FRUITING_TRIGGER: warning (yellow #ffc107) - environmental shift
 * - PRIMORDIA: light (light gray + custom) - pin formation
 * - FRUITING_BODY_GROWTH: primary + custom - active growth
 * - HARVEST: success (bright green) - harvesting phase
 * - REHYDRATION_PAUSE: info + custom - resting between flushes
 * - COMPLETED: secondary (gray #6c757d) - finished batch
 *
 * Special Cases:
 * - isContaminated: danger (red #dc3545) - overrides phase color
 */

interface StatusBadgeProps {
  phase: string;
  isContaminated?: boolean;
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ phase, isContaminated = false }) => {
  const getBadgeClassName = (): string => {
    // Contaminated always shows as danger/red (overrides phase)
    if (isContaminated) {
      return 'badge badge-contaminated';
    }

    // Map 10 distinct phases to custom badge classes
    switch (phase) {
      // Phase 1: Starting
      case 'INOCULATION':
        return 'badge badge-phase-inoculation'; // blue

      // Phase 2-3: Colonization (early to full)
      case 'EARLY_COLONIZATION':
        return 'badge badge-phase-early-colonization'; // teal

      case 'FULL_COLONIZATION':
        return 'badge badge-phase-full-colonization'; // green

      // Phase 4: Consolidation
      case 'CONSOLIDATION':
        return 'badge badge-phase-consolidation'; // dark gray

      // Phase 5-7: Fruiting stages
      case 'FRUITING_TRIGGER':
        return 'badge badge-phase-fruiting-trigger'; // yellow

      case 'PRIMORDIA':
        return 'badge badge-phase-primordia'; // orange

      case 'FRUITING_BODY_GROWTH':
        return 'badge badge-phase-fruiting-body-growth'; // purple

      // Phase 8: Harvest
      case 'HARVEST':
        return 'badge badge-phase-harvest'; // bright green

      // Phase 9: Rehydration
      case 'REHYDRATION_PAUSE':
        return 'badge badge-phase-rehydration-pause'; // cyan

      // Phase 10: Completed
      case 'COMPLETED':
        return 'badge badge-phase-completed'; // gray

      // Legacy/fallback
      default:
        return 'badge badge-secondary'; // gray for unknown phases
    }
  };

  const getAriaLabel = (): string => {
    if (isContaminated) {
      return `Phase: ${phase} (contaminated)`;
    }
    return `Phase: ${phase}`;
  };

  return (
    <span className={`${getBadgeClassName()} rounded-pill`} aria-label={getAriaLabel()}>
      {phase}
    </span>
  );
};

export default StatusBadge;
