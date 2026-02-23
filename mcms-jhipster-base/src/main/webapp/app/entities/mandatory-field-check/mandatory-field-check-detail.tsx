import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './mandatory-field-check.reducer';

export const MandatoryFieldCheckDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const mandatoryFieldCheckEntity = useAppSelector(state => state.mandatoryFieldCheck.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="mandatoryFieldCheckDetailsHeading">Mandatory Field Check</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{mandatoryFieldCheckEntity.id}</dd>
          <dt>
            <span id="fieldName">Field Name</span>
            <UncontrolledTooltip target="fieldName">Which field was checked</UncontrolledTooltip>
          </dt>
          <dd>{mandatoryFieldCheckEntity.fieldName}</dd>
          <dt>
            <span id="isFilled">Is Filled</span>
            <UncontrolledTooltip target="isFilled">Was it filled in?</UncontrolledTooltip>
          </dt>
          <dd>{mandatoryFieldCheckEntity.isFilled ? 'true' : 'false'}</dd>
          <dt>
            <span id="checkDate">Check Date</span>
            <UncontrolledTooltip target="checkDate">When the check was performed</UncontrolledTooltip>
          </dt>
          <dd>
            {mandatoryFieldCheckEntity.checkDate ? (
              <TextFormat value={mandatoryFieldCheckEntity.checkDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>Phase Execution</dt>
          <dd>{mandatoryFieldCheckEntity.phaseExecution ? mandatoryFieldCheckEntity.phaseExecution.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/mandatory-field-check" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/mandatory-field-check/${mandatoryFieldCheckEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default MandatoryFieldCheckDetail;
