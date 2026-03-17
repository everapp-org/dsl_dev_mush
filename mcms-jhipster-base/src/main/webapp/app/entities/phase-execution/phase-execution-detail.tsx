import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './phase-execution.reducer';

export const PhaseExecutionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const phaseExecutionEntity = useAppSelector(state => state.phaseExecution.entity);
  return (
    <Row>
      <Col md="8">
        <h1 data-cy="phaseExecutionDetailsHeading">Phase Execution</h1>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{phaseExecutionEntity.id}</dd>
          <dt>
            <span id="phase">Phase</span>
            <UncontrolledTooltip target="phase">Which lifecycle phase</UncontrolledTooltip>
          </dt>
          <dd>{phaseExecutionEntity.phase}</dd>
          <dt>
            <span id="sequenceOrder">Sequence Order</span>
            <UncontrolledTooltip target="sequenceOrder">Execution order within the batch (1, 2, 3...)</UncontrolledTooltip>
          </dt>
          <dd>{phaseExecutionEntity.sequenceOrder}</dd>
          <dt>
            <span id="startDate">Start Date</span>
            <UncontrolledTooltip target="startDate">Phase start date</UncontrolledTooltip>
          </dt>
          <dd>
            {phaseExecutionEntity.startDate ? (
              <TextFormat value={phaseExecutionEntity.startDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="endDate">End Date</span>
            <UncontrolledTooltip target="endDate">Phase end date (null if ongoing)</UncontrolledTooltip>
          </dt>
          <dd>
            {phaseExecutionEntity.endDate ? (
              <TextFormat value={phaseExecutionEntity.endDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="plannedDurationDays">Planned Duration Days</span>
            <UncontrolledTooltip target="plannedDurationDays">Planned duration in days</UncontrolledTooltip>
          </dt>
          <dd>{phaseExecutionEntity.plannedDurationDays}</dd>
          <dt>
            <span id="actualDurationDays">Actual Duration Days</span>
            <UncontrolledTooltip target="actualDurationDays">Actual duration in days</UncontrolledTooltip>
          </dt>
          <dd>{phaseExecutionEntity.actualDurationDays}</dd>
          <dt>
            <span id="responsiblePerson">Responsible Person</span>
            <UncontrolledTooltip target="responsiblePerson">Who managed this phase (display name)</UncontrolledTooltip>
          </dt>
          <dd>{phaseExecutionEntity.responsiblePerson}</dd>
          <dt>
            <span id="note">Note</span>
            <UncontrolledTooltip target="note">Phase observations &amp; notes</UncontrolledTooltip>
          </dt>
          <dd>{phaseExecutionEntity.note}</dd>
          <dt>
            <span id="batch">Batch</span>
            <UncontrolledTooltip target="batch">Batch this phase execution belongs to</UncontrolledTooltip>
          </dt>
          <dd>
            {phaseExecutionEntity.batch ? (
              <Link to={`/batch/${phaseExecutionEntity.batch.id}`}>{phaseExecutionEntity.batch.batchCode}</Link>
            ) : (
              ''
            )}
          </dd>
          <dt>
            <span id="room">Room</span>
            <UncontrolledTooltip target="room">Room where this phase was executed</UncontrolledTooltip>
          </dt>
          <dd>
            {phaseExecutionEntity.room ? <Link to={`/room/${phaseExecutionEntity.room.id}`}>{phaseExecutionEntity.room.name}</Link> : ''}
          </dd>
        </dl>
        <Button tag={Link} to="/phase-execution" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/phase-execution/${phaseExecutionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default PhaseExecutionDetail;
