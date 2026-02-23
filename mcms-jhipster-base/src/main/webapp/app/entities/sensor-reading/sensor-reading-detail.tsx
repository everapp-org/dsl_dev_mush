import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './sensor-reading.reducer';

export const SensorReadingDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const sensorReadingEntity = useAppSelector(state => state.sensorReading.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="sensorReadingDetailsHeading">Sensor Reading</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{sensorReadingEntity.id}</dd>
          <dt>
            <span id="timestamp">Timestamp</span>
            <UncontrolledTooltip target="timestamp">Reading timestamp (UTC)</UncontrolledTooltip>
          </dt>
          <dd>
            {sensorReadingEntity.timestamp ? (
              <TextFormat value={sensorReadingEntity.timestamp} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="value">Value</span>
            <UncontrolledTooltip target="value">Measured value</UncontrolledTooltip>
          </dt>
          <dd>{sensorReadingEntity.value}</dd>
          <dt>
            <span id="unit">Unit</span>
            <UncontrolledTooltip target="unit">Unit of measurement</UncontrolledTooltip>
          </dt>
          <dd>{sensorReadingEntity.unit}</dd>
          <dt>Sensor</dt>
          <dd>{sensorReadingEntity.sensor ? sensorReadingEntity.sensor.sensorCode : ''}</dd>
        </dl>
        <Button tag={Link} to="/sensor-reading" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/sensor-reading/${sensorReadingEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default SensorReadingDetail;
