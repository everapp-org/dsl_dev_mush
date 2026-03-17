import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip, Table } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT, AUTHORITIES } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';

import { getEntity, getBatchesInRoom, getSensorsInRoom, getAlertsInRoom } from './room.reducer';

export const RoomDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
    dispatch(getBatchesInRoom(id));
    dispatch(getSensorsInRoom(id));
    dispatch(getAlertsInRoom(id));
  }, []);

  const roomEntity = useAppSelector(state => state.room.entity);
  const batchesInRoom = useAppSelector(state => state.room.batchesInRoom);
  const sensorsInRoom = useAppSelector(state => state.room.sensorsInRoom);
  const alertsInRoom = useAppSelector(state => state.room.alertsInRoom);
  const isAdmin = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));

  return (
    <Row>
      <Col md="8">
        <h2 data-cy="roomDetailsHeading">Room</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{roomEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
            <UncontrolledTooltip target="name">e.g. &#34;Incubation A1&#34;, &#34;Chamber B3&#34;</UncontrolledTooltip>
          </dt>
          <dd>{roomEntity.name}</dd>
          <dt>
            <span id="code">Code</span>
            <UncontrolledTooltip target="code">Short code, e.g. &#34;INC-A1&#34;</UncontrolledTooltip>
          </dt>
          <dd>{roomEntity.code}</dd>
          <dt>
            <span id="roomType">Room Type</span>
            <UncontrolledTooltip target="roomType">Functional classification</UncontrolledTooltip>
          </dt>
          <dd>{roomEntity.roomType}</dd>
          <dt>
            <span id="status">Status</span>
            <UncontrolledTooltip target="status">Current operational status</UncontrolledTooltip>
          </dt>
          <dd>{roomEntity.status}</dd>
          <dt>
            <span id="capacityBags">Capacity Bags</span>
            <UncontrolledTooltip target="capacityBags">Max bag/block capacity</UncontrolledTooltip>
          </dt>
          <dd>{roomEntity.capacityBags}</dd>
          <dt>
            <span id="currentOccupancy">Current Occupancy</span>
            <UncontrolledTooltip target="currentOccupancy">Current number of bags/blocks</UncontrolledTooltip>
          </dt>
          <dd>{roomEntity.currentOccupancy}</dd>
          <dt>
            <span id="areaSqM">Area Sq M</span>
            <UncontrolledTooltip target="areaSqM">Floor area in m²</UncontrolledTooltip>
          </dt>
          <dd>{roomEntity.areaSqM}</dd>
          <dt>
            <span id="hasHVAC">Has HVAC</span>
            <UncontrolledTooltip target="hasHVAC">Automated climate control?</UncontrolledTooltip>
          </dt>
          <dd>{roomEntity.hasHVAC ? 'true' : 'false'}</dd>
          <dt>
            <span id="hasMisting">Has Misting</span>
            <UncontrolledTooltip target="hasMisting">Automated misting/humidification?</UncontrolledTooltip>
          </dt>
          <dd>{roomEntity.hasMisting ? 'true' : 'false'}</dd>
          <dt>
            <span id="lastDisinfectionDate">Last Disinfection Date</span>
            <UncontrolledTooltip target="lastDisinfectionDate">When was it last disinfected?</UncontrolledTooltip>
          </dt>
          <dd>
            {roomEntity.lastDisinfectionDate ? (
              <TextFormat value={roomEntity.lastDisinfectionDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="note">Note</span>
          </dt>
          <dd>{roomEntity.note}</dd>
        </dl>
        <h3 className="mt-4">Current Occupancy</h3>
        {batchesInRoom && batchesInRoom.length > 0 ? (
          <>
            <p>
              <strong>Batches in Room:</strong> {batchesInRoom.length} / Capacity: {roomEntity.capacityBags || 'N/A'} bags
            </p>
            <Table responsive striped>
              <thead>
                <tr>
                  <th>Batch Code</th>
                  <th>Strain</th>
                  <th>Current Phase</th>
                  <th>Start Date</th>
                  <th>Bag Count</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {batchesInRoom.map(batch => (
                  <tr key={batch.id}>
                    <td>
                      <Link to={`/batch/${batch.id}`}>{batch.batchCode}</Link>
                    </td>
                    <td>{batch.strain ? batch.strain.name : ''}</td>
                    <td>{batch.currentPhase}</td>
                    <td>{batch.startDate ? <TextFormat value={batch.startDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</td>
                    <td>{batch.numberOfBags}</td>
                    <td>
                      <Button tag={Link} to={`/batch/${batch.id}`} color="info" size="sm">
                        <FontAwesomeIcon icon="eye" /> View
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          </>
        ) : (
          <p className="text-muted">No batches currently in this room.</p>
        )}
        <h3 className="mt-4">Installed Sensors</h3>
        {sensorsInRoom && sensorsInRoom.length > 0 ? (
          <Table responsive striped>
            <thead>
              <tr>
                <th>Sensor Code</th>
                <th>Type</th>
                <th>Status</th>
                <th>Manufacturer</th>
                <th>Model</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {sensorsInRoom.map(sensor => (
                <tr key={sensor.id}>
                  <td>
                    <Link to={`/sensor/${sensor.id}`}>{sensor.sensorCode}</Link>
                  </td>
                  <td>{sensor.sensorType}</td>
                  <td>{sensor.status}</td>
                  <td>{sensor.manufacturer}</td>
                  <td>{sensor.model}</td>
                  <td>
                    <Button tag={Link} to={`/sensor/${sensor.id}`} color="info" size="sm">
                      <FontAwesomeIcon icon="eye" /> View
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          <p className="text-muted">No sensors installed in this room.</p>
        )}
        <h3 className="mt-4">Environmental Alerts</h3>
        {alertsInRoom && alertsInRoom.length > 0 ? (
          <Table responsive striped>
            <thead>
              <tr>
                <th>Alert Time</th>
                <th>Severity</th>
                <th>Parameter</th>
                <th>Actual Value</th>
                <th>Threshold</th>
                <th>Message</th>
                <th>Acknowledged</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {alertsInRoom.map(alert => (
                <tr key={alert.id}>
                  <td>{alert.alertTime ? <TextFormat value={alert.alertTime} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</td>
                  <td>
                    <span
                      className={`badge bg-${alert.severity === 'CRITICAL' ? 'danger' : alert.severity === 'WARNING' ? 'warning' : 'info'}`}
                    >
                      {alert.severity}
                    </span>
                  </td>
                  <td>{alert.parameter}</td>
                  <td>{alert.actualValue}</td>
                  <td>{alert.thresholdValue}</td>
                  <td>{alert.message}</td>
                  <td>{alert.acknowledged ? 'Yes' : 'No'}</td>
                  <td>
                    <Button tag={Link} to={`/environmental-alert/${alert.id}`} color="info" size="sm">
                      <FontAwesomeIcon icon="eye" /> View
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          <p className="text-muted">No alerts for this room.</p>
        )}
        <Button tag={Link} to="/room" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        {isAdmin && (
          <Button tag={Link} to={`/room/${roomEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
          </Button>
        )}
      </Col>
    </Row>
  );
};

export default RoomDetail;
