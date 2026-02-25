import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './sensor.reducer';

export const SensorDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const sensorEntity = useAppSelector(state => state.sensor.entity);
  const canManageSensors = useAppSelector(state =>
    hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN, AUTHORITIES.MANAGER]),
  );
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="sensorDetailsHeading">Sensor</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{sensorEntity.id}</dd>
          <dt>
            <span id="sensorCode">Sensor Code</span>
            <UncontrolledTooltip target="sensorCode">Device identifier</UncontrolledTooltip>
          </dt>
          <dd>{sensorEntity.sensorCode}</dd>
          <dt>
            <span id="sensorType">Sensor Type</span>
            <UncontrolledTooltip target="sensorType">What it measures</UncontrolledTooltip>
          </dt>
          <dd>{sensorEntity.sensorType}</dd>
          <dt>
            <span id="status">Status</span>
            <UncontrolledTooltip target="status">Current operational status</UncontrolledTooltip>
          </dt>
          <dd>{sensorEntity.status}</dd>
          <dt>
            <span id="installedDate">Installed Date</span>
            <UncontrolledTooltip target="installedDate">Installation date</UncontrolledTooltip>
          </dt>
          <dd>
            {sensorEntity.installedDate ? (
              <TextFormat value={sensorEntity.installedDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="lastCalibrationDate">Last Calibration Date</span>
            <UncontrolledTooltip target="lastCalibrationDate">Last calibration date</UncontrolledTooltip>
          </dt>
          <dd>
            {sensorEntity.lastCalibrationDate ? (
              <TextFormat value={sensorEntity.lastCalibrationDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="manufacturer">Manufacturer</span>
            <UncontrolledTooltip target="manufacturer">Device manufacturer</UncontrolledTooltip>
          </dt>
          <dd>{sensorEntity.manufacturer}</dd>
          <dt>
            <span id="model">Model</span>
            <UncontrolledTooltip target="model">Device model</UncontrolledTooltip>
          </dt>
          <dd>{sensorEntity.model}</dd>
          <dt>
            <span id="note">Note</span>
          </dt>
          <dd>{sensorEntity.note}</dd>
          <dt>
            <span id="room">Assigned Room</span>
            <UncontrolledTooltip target="room">Room where sensor is installed</UncontrolledTooltip>
          </dt>
          <dd>
            {sensorEntity.room ? (
              <Link to={`/room/${sensorEntity.room.id}`}>{sensorEntity.room.name}</Link>
            ) : (
              <span className="text-muted">No room assigned</span>
            )}
          </dd>
        </dl>
        <Button tag={Link} to="/sensor" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        {canManageSensors && (
          <>
            &nbsp;
            <Button tag={Link} to={`/sensor/${sensorEntity.id}/edit`} replace color="primary">
              <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
            </Button>
          </>
        )}
      </Col>
    </Row>
  );
};

export default SensorDetail;
