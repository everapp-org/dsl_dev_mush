import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './environmental-alert.reducer';

export const EnvironmentalAlertDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const environmentalAlertEntity = useAppSelector(state => state.environmentalAlert.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="environmentalAlertDetailsHeading">Environmental Alert</h2>
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
            {environmentalAlertEntity.alertTime ? (
              <TextFormat value={environmentalAlertEntity.alertTime} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="severity">Severity</span>
            <UncontrolledTooltip target="severity">INFO / WARNING / CRITICAL</UncontrolledTooltip>
          </dt>
          <dd>{environmentalAlertEntity.severity}</dd>
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
              <TextFormat value={environmentalAlertEntity.acknowledgedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="resolutionNote">Resolution Note</span>
            <UncontrolledTooltip target="resolutionNote">What was done about it</UncontrolledTooltip>
          </dt>
          <dd>{environmentalAlertEntity.resolutionNote}</dd>
          <dt>Room</dt>
          <dd>{environmentalAlertEntity.room ? environmentalAlertEntity.room.name : ''}</dd>
          <dt>Sensor</dt>
          <dd>{environmentalAlertEntity.sensor ? environmentalAlertEntity.sensor.sensorCode : ''}</dd>
          <dt>Batch</dt>
          <dd>{environmentalAlertEntity.batch ? environmentalAlertEntity.batch.batchCode : ''}</dd>
        </dl>
        <Button tag={Link} to="/environmental-alert" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/environmental-alert/${environmentalAlertEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default EnvironmentalAlertDetail;
