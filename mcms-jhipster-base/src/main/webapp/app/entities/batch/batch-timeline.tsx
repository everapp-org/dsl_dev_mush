import './batch-timeline.scss';

import React from 'react';
import { TextFormat } from 'react-jhipster';
import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

interface PhaseExecution {
  id: number;
  phase: string;
  startDate: string;
  endDate?: string;
  sequenceOrder: number;
  actualDurationDays?: number;
  room?: { name: string };
  responsiblePerson?: string;
}

interface BatchTimelineProps {
  phaseExecutions: PhaseExecution[];
  currentPhase: string;
}

// Phase color mapping (matches phase-badge-colors.scss)
const getPhaseColor = (phase: string): string => {
  const colors: Record<string, string> = {
    INOCULATION: '#0d6efd',
    EARLY_COLONIZATION: '#20c997',
    FULL_COLONIZATION: '#28a745',
    CONSOLIDATION: '#495057',
    FRUITING_TRIGGER: '#ffc107',
    PRIMORDIA: '#fd7e14',
    FRUITING_BODY_GROWTH: '#6f42c1',
    HARVEST: '#198754',
    REHYDRATION_PAUSE: '#0dcaf0',
    COMPLETED: '#6c757d',
  };
  return colors[phase] || '#6c757d';
};

// Phase display names (user-friendly)
const getPhaseDisplayName = (phase: string): string => {
  const displayNames: Record<string, string> = {
    INOCULATION: 'Inoculation',
    EARLY_COLONIZATION: 'Early Colonization',
    FULL_COLONIZATION: 'Full Colonization',
    CONSOLIDATION: 'Consolidation',
    FRUITING_TRIGGER: 'Fruiting Trigger',
    PRIMORDIA: 'Primordia',
    FRUITING_BODY_GROWTH: 'Fruiting Growth',
    HARVEST: 'Harvest',
    REHYDRATION_PAUSE: 'Rehydration',
    COMPLETED: 'Completed',
  };
  return displayNames[phase] || phase.replace(/_/g, ' ');
};

export const BatchTimeline: React.FC<BatchTimelineProps> = ({ phaseExecutions, currentPhase }) => {
  if (!phaseExecutions || phaseExecutions.length === 0) {
    return (
      <div className="batch-timeline-empty">
        <p className="text-muted">No phase execution data available yet. Timeline will appear once the batch progresses through phases.</p>
      </div>
    );
  }

  // Sort by sequence order
  const sortedPhases = [...phaseExecutions].sort((a, b) => a.sequenceOrder - b.sequenceOrder);

  return (
    <div className="batch-timeline-container">
      <h3 className="timeline-title">Batch Lifecycle Timeline</h3>
      <p className="timeline-subtitle text-muted">
        Visual representation of the 10-phase mushroom cultivation lifecycle showing progression through {sortedPhases.length} completed
        phases
      </p>

      <div className="timeline-horizontal">
        {sortedPhases.map((phase, index) => {
          const isCurrentPhase = phase.phase === currentPhase;
          const isCompleted = phase.endDate != null;
          const phaseColor = getPhaseColor(phase.phase);

          return (
            <div
              key={phase.id}
              className={`timeline-phase ${isCurrentPhase ? 'current-phase' : ''} ${isCompleted ? 'completed' : 'in-progress'}`}
            >
              {/* Phase bar */}
              <div className="phase-bar-wrapper">
                <div className="phase-bar" style={{ backgroundColor: phaseColor }}>
                  <span className="phase-number">{phase.sequenceOrder}</span>
                </div>
                {index < sortedPhases.length - 1 && <div className="phase-connector" />}
              </div>

              {/* Phase info card */}
              <div className="phase-info-card">
                <div className="phase-name" style={{ borderColor: phaseColor }}>
                  {getPhaseDisplayName(phase.phase)}
                </div>

                <div className="phase-details">
                  <div className="phase-detail-item">
                    <span className="detail-label">Start:</span>
                    <span className="detail-value">
                      <TextFormat value={phase.startDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
                    </span>
                  </div>

                  {phase.endDate && (
                    <div className="phase-detail-item">
                      <span className="detail-label">End:</span>
                      <span className="detail-value">
                        <TextFormat value={phase.endDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
                      </span>
                    </div>
                  )}

                  {phase.actualDurationDays && (
                    <div className="phase-detail-item">
                      <span className="detail-label">Duration:</span>
                      <span className="detail-value">{phase.actualDurationDays} days</span>
                    </div>
                  )}

                  {phase.room && (
                    <div className="phase-detail-item">
                      <span className="detail-label">Room:</span>
                      <span className="detail-value">{phase.room.name}</span>
                    </div>
                  )}

                  {phase.responsiblePerson && (
                    <div className="phase-detail-item">
                      <span className="detail-label">By:</span>
                      <span className="detail-value">{phase.responsiblePerson}</span>
                    </div>
                  )}
                </div>

                {isCurrentPhase && (
                  <div className="current-phase-badge" style={{ backgroundColor: phaseColor }}>
                    Current Phase
                  </div>
                )}
              </div>
            </div>
          );
        })}
      </div>

      {/* Legend */}
      <div className="timeline-legend">
        <div className="legend-item">
          <div className="legend-indicator completed-indicator" />
          <span>Completed Phase</span>
        </div>
        <div className="legend-item">
          <div className="legend-indicator in-progress-indicator" />
          <span>In Progress</span>
        </div>
        <div className="legend-item">
          <div className="legend-indicator current-indicator" />
          <span>Current Phase</span>
        </div>
      </div>
    </div>
  );
};

export default BatchTimeline;
