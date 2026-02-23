import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './environmental-target.reducer';

export const EnvironmentalTargetDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const environmentalTargetEntity = useAppSelector(state => state.environmentalTarget.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="environmentalTargetDetailsHeading">Environmental Target</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{environmentalTargetEntity.id}</dd>
          <dt>
            <span id="phase">Phase</span>
            <UncontrolledTooltip target="phase">Target applies during this phase</UncontrolledTooltip>
          </dt>
          <dd>{environmentalTargetEntity.phase}</dd>
          <dt>
            <span id="tempMinC">Temp Min C</span>
            <UncontrolledTooltip target="tempMinC">Minimum temperature °C</UncontrolledTooltip>
          </dt>
          <dd>{environmentalTargetEntity.tempMinC}</dd>
          <dt>
            <span id="tempMaxC">Temp Max C</span>
            <UncontrolledTooltip target="tempMaxC">Maximum temperature °C</UncontrolledTooltip>
          </dt>
          <dd>{environmentalTargetEntity.tempMaxC}</dd>
          <dt>
            <span id="humidityMinPercent">Humidity Min Percent</span>
            <UncontrolledTooltip target="humidityMinPercent">Minimum RH %</UncontrolledTooltip>
          </dt>
          <dd>{environmentalTargetEntity.humidityMinPercent}</dd>
          <dt>
            <span id="humidityMaxPercent">Humidity Max Percent</span>
            <UncontrolledTooltip target="humidityMaxPercent">Maximum RH %</UncontrolledTooltip>
          </dt>
          <dd>{environmentalTargetEntity.humidityMaxPercent}</dd>
          <dt>
            <span id="co2MaxPpm">Co 2 Max Ppm</span>
            <UncontrolledTooltip target="co2MaxPpm">Maximum CO₂ ppm</UncontrolledTooltip>
          </dt>
          <dd>{environmentalTargetEntity.co2MaxPpm}</dd>
          <dt>
            <span id="lightLux">Light Lux</span>
            <UncontrolledTooltip target="lightLux">Target light level</UncontrolledTooltip>
          </dt>
          <dd>{environmentalTargetEntity.lightLux}</dd>
          <dt>
            <span id="freshAirExchangesPerHour">Fresh Air Exchanges Per Hour</span>
            <UncontrolledTooltip target="freshAirExchangesPerHour">FAE target</UncontrolledTooltip>
          </dt>
          <dd>{environmentalTargetEntity.freshAirExchangesPerHour}</dd>
          <dt>Room</dt>
          <dd>{environmentalTargetEntity.room ? environmentalTargetEntity.room.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/environmental-target" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/environmental-target/${environmentalTargetEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default EnvironmentalTargetDetail;
