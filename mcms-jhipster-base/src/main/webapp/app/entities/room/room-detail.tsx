import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './room.reducer';

export const RoomDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const roomEntity = useAppSelector(state => state.room.entity);
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
        <Button tag={Link} to="/room" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/room/${roomEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default RoomDetail;
