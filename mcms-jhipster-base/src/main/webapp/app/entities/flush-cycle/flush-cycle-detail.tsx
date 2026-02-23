import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './flush-cycle.reducer';

export const FlushCycleDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const flushCycleEntity = useAppSelector(state => state.flushCycle.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="flushCycleDetailsHeading">Flush Cycle</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{flushCycleEntity.id}</dd>
          <dt>
            <span id="flushNumber">Flush Number</span>
            <UncontrolledTooltip target="flushNumber">1st, 2nd, 3rd flush, etc.</UncontrolledTooltip>
          </dt>
          <dd>{flushCycleEntity.flushNumber}</dd>
          <dt>
            <span id="harvestStartDate">Harvest Start Date</span>
            <UncontrolledTooltip target="harvestStartDate">When picking began</UncontrolledTooltip>
          </dt>
          <dd>
            {flushCycleEntity.harvestStartDate ? (
              <TextFormat value={flushCycleEntity.harvestStartDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="harvestEndDate">Harvest End Date</span>
            <UncontrolledTooltip target="harvestEndDate">When picking ended</UncontrolledTooltip>
          </dt>
          <dd>
            {flushCycleEntity.harvestEndDate ? (
              <TextFormat value={flushCycleEntity.harvestEndDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="yieldKg">Yield Kg</span>
            <UncontrolledTooltip target="yieldKg">Total yield for this flush</UncontrolledTooltip>
          </dt>
          <dd>{flushCycleEntity.yieldKg}</dd>
          <dt>
            <span id="yieldBagsHarvested">Yield Bags Harvested</span>
            <UncontrolledTooltip target="yieldBagsHarvested">Number of bags that produced</UncontrolledTooltip>
          </dt>
          <dd>{flushCycleEntity.yieldBagsHarvested}</dd>
          <dt>
            <span id="avgFruitBodyWeightG">Avg Fruit Body Weight G</span>
            <UncontrolledTooltip target="avgFruitBodyWeightG">Average individual mushroom weight in grams</UncontrolledTooltip>
          </dt>
          <dd>{flushCycleEntity.avgFruitBodyWeightG}</dd>
          <dt>
            <span id="rehydrationDone">Rehydration Done</span>
            <UncontrolledTooltip target="rehydrationDone">Was rehydration performed after this flush?</UncontrolledTooltip>
          </dt>
          <dd>{flushCycleEntity.rehydrationDone ? 'true' : 'false'}</dd>
          <dt>
            <span id="rehydrationDurationHours">Rehydration Duration Hours</span>
            <UncontrolledTooltip target="rehydrationDurationHours">Soaking time in hours</UncontrolledTooltip>
          </dt>
          <dd>{flushCycleEntity.rehydrationDurationHours}</dd>
          <dt>
            <span id="note">Note</span>
            <UncontrolledTooltip target="note">Flush-specific observations</UncontrolledTooltip>
          </dt>
          <dd>{flushCycleEntity.note}</dd>
          <dt>Batch</dt>
          <dd>{flushCycleEntity.batch ? flushCycleEntity.batch.batchCode : ''}</dd>
        </dl>
        <Button tag={Link} to="/flush-cycle" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/flush-cycle/${flushCycleEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default FlushCycleDetail;
