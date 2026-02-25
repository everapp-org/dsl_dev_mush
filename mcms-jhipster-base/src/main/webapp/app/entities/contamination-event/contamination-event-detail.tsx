import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './contamination-event.reducer';

export const ContaminationEventDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const contaminationEventEntity = useAppSelector(state => state.contaminationEvent.entity);
  const isAdminOrManager = useAppSelector(state =>
    hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN, AUTHORITIES.MANAGER]),
  );
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="contaminationEventDetailsHeading">Contamination Event</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{contaminationEventEntity.id}</dd>
          <dt>
            <span id="detectedDate">Detected Date</span>
            <UncontrolledTooltip target="detectedDate">When contamination was first observed</UncontrolledTooltip>
          </dt>
          <dd>
            {contaminationEventEntity.detectedDate ? (
              <TextFormat value={contaminationEventEntity.detectedDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="type">Type</span>
            <UncontrolledTooltip target="type">What type of contamination</UncontrolledTooltip>
          </dt>
          <dd>{contaminationEventEntity.type}</dd>
          <dt>
            <span id="severity">Severity</span>
            <UncontrolledTooltip target="severity">How severe</UncontrolledTooltip>
          </dt>
          <dd>{contaminationEventEntity.severity}</dd>
          <dt>
            <span id="affectedBags">Affected Bags</span>
            <UncontrolledTooltip target="affectedBags">How many bags/blocks affected</UncontrolledTooltip>
          </dt>
          <dd>{contaminationEventEntity.affectedBags}</dd>
          <dt>
            <span id="affectedPercentage">Affected Percentage</span>
            <UncontrolledTooltip target="affectedPercentage">% of batch affected</UncontrolledTooltip>
          </dt>
          <dd>{contaminationEventEntity.affectedPercentage}</dd>
          <dt>
            <span id="actionTaken">Action Taken</span>
            <UncontrolledTooltip target="actionTaken">Primary response action</UncontrolledTooltip>
          </dt>
          <dd>{contaminationEventEntity.actionTaken}</dd>
          <dt>
            <span id="resolvedDate">Resolved Date</span>
            <UncontrolledTooltip target="resolvedDate">When issue was resolved (null if ongoing)</UncontrolledTooltip>
          </dt>
          <dd>
            {contaminationEventEntity.resolvedDate ? (
              <TextFormat value={contaminationEventEntity.resolvedDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="lossKg">Loss Kg</span>
            <UncontrolledTooltip target="lossKg">Estimated yield loss in kg</UncontrolledTooltip>
          </dt>
          <dd>{contaminationEventEntity.lossKg}</dd>
          <dt>
            <span id="rootCauseAnalysis">Root Cause Analysis</span>
            <UncontrolledTooltip target="rootCauseAnalysis">Root cause investigation notes</UncontrolledTooltip>
          </dt>
          <dd>{contaminationEventEntity.rootCauseAnalysis}</dd>
          <dt>
            <span id="preventiveMeasures">Preventive Measures</span>
            <UncontrolledTooltip target="preventiveMeasures">Preventive actions for future</UncontrolledTooltip>
          </dt>
          <dd>{contaminationEventEntity.preventiveMeasures}</dd>
          <dt>
            <span id="detectedBy">Detected By</span>
            <UncontrolledTooltip target="detectedBy">Who discovered it</UncontrolledTooltip>
          </dt>
          <dd>{contaminationEventEntity.detectedBy}</dd>
          <dt>
            <span id="photosReference">Photos Reference</span>
            <UncontrolledTooltip target="photosReference">Path/URL to photo evidence</UncontrolledTooltip>
          </dt>
          <dd>{contaminationEventEntity.photosReference}</dd>
          <dt>
            <span id="note">Note</span>
          </dt>
          <dd>{contaminationEventEntity.note}</dd>
          <dt>Batch</dt>
          <dd>
            {contaminationEventEntity.batch ? (
              <Link to={`/batch/${contaminationEventEntity.batch.id}`}>{contaminationEventEntity.batch.batchCode}</Link>
            ) : (
              ''
            )}
          </dd>
          <dt>Phase Execution</dt>
          <dd>{contaminationEventEntity.phaseExecution ? contaminationEventEntity.phaseExecution.id : ''}</dd>
          <dt>Room</dt>
          <dd>
            {contaminationEventEntity.room ? (
              <Link to={`/room/${contaminationEventEntity.room.id}`}>{contaminationEventEntity.room.name}</Link>
            ) : (
              ''
            )}
          </dd>
        </dl>
        <Button tag={Link} to="/contamination-event" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        {isAdminOrManager && (
          <>
            &nbsp;
            <Button tag={Link} to={`/contamination-event/${contaminationEventEntity.id}/edit`} replace color="primary">
              <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
            </Button>
          </>
        )}
      </Col>
    </Row>
  );
};

export default ContaminationEventDetail;
