import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip, Badge } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, AUTHORITIES } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { RelativeTime } from 'app/shared/util/relative-time';

import { getEntity } from './environmental-alert.reducer';

// Feature #391: Helper function to get badge color based on severity
const getSeverityBadgeColor = (severity: string): string => {
  switch (severity) {
    case 'INFO':
      return 'info';
    case 'WARNING':
      return 'warning';
    case 'CRITICAL':
      return 'danger';
    default:
      return 'secondary';
  }
};

export const EnvironmentalAlertDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const environmentalAlertEntity = useAppSelector(state => state.environmentalAlert.entity);
  const canAcknowledge = useAppSelector(state =>
    hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN, AUTHORITIES.MANAGER, AUTHORITIES.OPERATOR]),
  );
  return (
    <Row>
      <Col md="8">
        <h1 data-cy="environmentalAlertDetailsHeading">Environmental Alert</h1>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{environmentalAlertEntity.id}</dd>
          <dt>
            <span id="alertTime">Alert Time</span>
            <UncontrolledTooltip target="alertTime">When the alert was triggered</UncontrolledTooltip>
          </dt>
          <dd>
            {environmentalAlertEntity.alertTime ? <RelativeTime value={environmentalAlertEntity.alertTime} id="alert-time-detail" /> : null}
          </dd>
          <dt>
            <span id="severity">Severity</span>
            <UncontrolledTooltip target="severity">INFO / WARNING / CRITICAL</UncontrolledTooltip>
          </dt>
          <dd>
            {environmentalAlertEntity.severity && (
              <Badge color={getSeverityBadgeColor(environmentalAlertEntity.severity)}>{environmentalAlertEntity.severity}</Badge>
            )}
          </dd>
          <dt>
            <span id="parameter">Parameter</span>
            <UncontrolledTooltip target="parameter">Which environmental parameter</UncontrolledTooltip>
          </dt>
          <dd>{environmentalAlertEntity.parameter}</dd>
          <dt>
            <span id="actualValue">Actual Value</span>
            <UncontrolledTooltip target="actualValue">The value that triggered the alert</UncontrolledTooltip>
          </dt>
          <dd>{environmentalAlertEntity.actualValue}</dd>
          <dt>
            <span id="thresholdValue">Threshold Value</span>
            <UncontrolledTooltip target="thresholdValue">The threshold that was breached</UncontrolledTooltip>
          </dt>
          <dd>{environmentalAlertEntity.thresholdValue}</dd>
          <dt>
            <span id="message">Message</span>
            <UncontrolledTooltip target="message">Human-readable alert description</UncontrolledTooltip>
          </dt>
          <dd>{environmentalAlertEntity.message}</dd>
          <dt>
            <span id="acknowledged">Acknowledged</span>
            <UncontrolledTooltip target="acknowledged">Has someone acknowledged this?</UncontrolledTooltip>
          </dt>
          <dd>{environmentalAlertEntity.acknowledged ? 'true' : 'false'}</dd>
          <dt>
            <span id="acknowledgedBy">Acknowledged By</span>
            <UncontrolledTooltip target="acknowledgedBy">Who acknowledged it</UncontrolledTooltip>
          </dt>
          <dd>{environmentalAlertEntity.acknowledgedBy}</dd>
          <dt>
            <span id="acknowledgedAt">Acknowledged At</span>
            <UncontrolledTooltip target="acknowledgedAt">When it was acknowledged</UncontrolledTooltip>
          </dt>
          <dd>
            {environmentalAlertEntity.acknowledgedAt ? (
              <RelativeTime value={environmentalAlertEntity.acknowledgedAt} id="ack-time-detail" />
            ) : null}
          </dd>
          <dt>
            <span id="resolutionNote">Resolution Note</span>
            <UncontrolledTooltip target="resolutionNote">What was done about it</UncontrolledTooltip>
          </dt>
          <dd>{environmentalAlertEntity.resolutionNote}</dd>
          <dt>
            <span id="room">Room</span>
            <UncontrolledTooltip target="room">Room where alert occurred</UncontrolledTooltip>
          </dt>
          <dd>
            {environmentalAlertEntity.room ? (
              <Link to={`/room/${environmentalAlertEntity.room.id}`}>{environmentalAlertEntity.room.name}</Link>
            ) : (
              <span className="text-muted">No room</span>
            )}
          </dd>
          <dt>
            <span id="sensor">Sensor</span>
            <UncontrolledTooltip target="sensor">Sensor that triggered alert</UncontrolledTooltip>
          </dt>
          <dd>
            {environmentalAlertEntity.sensor ? (
              <Link to={`/sensor/${environmentalAlertEntity.sensor.id}`}>{environmentalAlertEntity.sensor.sensorCode}</Link>
            ) : (
              <span className="text-muted">No sensor linked</span>
            )}
          </dd>
          <dt>
            <span id="batch">Affected Batch</span>
            <UncontrolledTooltip target="batch">Batch affected by this alert</UncontrolledTooltip>
          </dt>
          <dd>
            {environmentalAlertEntity.batch ? (
              <Link to={`/batch/${environmentalAlertEntity.batch.id}`}>{environmentalAlertEntity.batch.batchCode}</Link>
            ) : (
              <span className="text-muted">No batch affected</span>
            )}
          </dd>
        </dl>
        <Button tag={Link} to="/environmental-alert" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        {canAcknowledge && !environmentalAlertEntity.acknowledged && (
          <>
            <Button tag={Link} to={`/environmental-alert/${environmentalAlertEntity.id}/acknowledge`} replace color="success">
              <FontAwesomeIcon icon="check" /> <span className="d-none d-md-inline">Acknowledge</span>
            </Button>
            &nbsp;
          </>
        )}
        <Button tag={Link} to={`/environmental-alert/${environmentalAlertEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default EnvironmentalAlertDetail;
