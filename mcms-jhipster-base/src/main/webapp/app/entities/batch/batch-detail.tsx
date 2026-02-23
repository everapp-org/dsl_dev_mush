import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './batch.reducer';

export const BatchDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const batchEntity = useAppSelector(state => state.batch.entity);
  const isAdmin = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));
  const canModifyBatch = useAppSelector(state =>
    hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN, AUTHORITIES.MANAGER, AUTHORITIES.OPERATOR]),
  );
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="batchDetailsHeading">Batch</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{batchEntity.id}</dd>
          <dt>
            <span id="batchCode">Batch Code</span>
            <UncontrolledTooltip target="batchCode">Unique identifier, e.g. &#34;PO-2025-001&#34;</UncontrolledTooltip>
          </dt>
          <dd>{batchEntity.batchCode}</dd>
          <dt>
            <span id="startDate">Start Date</span>
            <UncontrolledTooltip target="startDate">Inoculation date</UncontrolledTooltip>
          </dt>
          <dd>{batchEntity.startDate ? <TextFormat value={batchEntity.startDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="endDate">End Date</span>
            <UncontrolledTooltip target="endDate">Batch completion date</UncontrolledTooltip>
          </dt>
          <dd>{batchEntity.endDate ? <TextFormat value={batchEntity.endDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="currentPhase">Current Phase</span>
            <UncontrolledTooltip target="currentPhase">Current lifecycle phase (state machine)</UncontrolledTooltip>
          </dt>
          <dd>{batchEntity.currentPhase}</dd>
          <dt>
            <span id="numberOfBags">Number Of Bags</span>
            <UncontrolledTooltip target="numberOfBags">Total substrate bags/blocks in batch</UncontrolledTooltip>
          </dt>
          <dd>{batchEntity.numberOfBags}</dd>
          <dt>
            <span id="substrateWeightKg">Substrate Weight Kg</span>
            <UncontrolledTooltip target="substrateWeightKg">Total substrate weight in kg</UncontrolledTooltip>
          </dt>
          <dd>{batchEntity.substrateWeightKg}</dd>
          <dt>
            <span id="spawnWeightKg">Spawn Weight Kg</span>
            <UncontrolledTooltip target="spawnWeightKg">Total spawn weight used in kg</UncontrolledTooltip>
          </dt>
          <dd>{batchEntity.spawnWeightKg}</dd>
          <dt>
            <span id="targetYieldKg">Target Yield Kg</span>
            <UncontrolledTooltip target="targetYieldKg">Expected total yield in kg</UncontrolledTooltip>
          </dt>
          <dd>{batchEntity.targetYieldKg}</dd>
          <dt>
            <span id="actualTotalYieldKg">Actual Total Yield Kg</span>
            <UncontrolledTooltip target="actualTotalYieldKg">Accumulated actual yield across all flushes</UncontrolledTooltip>
          </dt>
          <dd>{batchEntity.actualTotalYieldKg}</dd>
          <dt>
            <span id="biologicalEfficiencyPercent">Biological Efficiency Percent</span>
            <UncontrolledTooltip target="biologicalEfficiencyPercent">(actual yield / dry substrate weight) × 100</UncontrolledTooltip>
          </dt>
          <dd>{batchEntity.biologicalEfficiencyPercent}</dd>
          <dt>
            <span id="isContaminated">Is Contaminated</span>
            <UncontrolledTooltip target="isContaminated">Flag: any contamination detected?</UncontrolledTooltip>
          </dt>
          <dd>{batchEntity.isContaminated ? 'true' : 'false'}</dd>
          <dt>
            <span id="isActive">Is Active</span>
            <UncontrolledTooltip target="isActive">Batch still in production?</UncontrolledTooltip>
          </dt>
          <dd>{batchEntity.isActive ? 'true' : 'false'}</dd>
          <dt>
            <span id="completionNote">Completion Note</span>
            <UncontrolledTooltip target="completionNote">Final notes upon batch closure</UncontrolledTooltip>
          </dt>
          <dd>{batchEntity.completionNote}</dd>
          <dt>
            <span id="note">Note</span>
            <UncontrolledTooltip target="note">General batch notes</UncontrolledTooltip>
          </dt>
          <dd>{batchEntity.note}</dd>
          <dt>Strain</dt>
          <dd>{batchEntity.strain ? batchEntity.strain.name : ''}</dd>
          <dt>Recipe</dt>
          <dd>{batchEntity.recipe ? batchEntity.recipe.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/batch" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        {canModifyBatch && (
          <>
            &nbsp;
            <Button tag={Link} to={`/batch/${batchEntity.id}/edit`} replace color="primary">
              <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
            </Button>
          </>
        )}
        {isAdmin && (
          <>
            &nbsp;
            <Button
              tag={Link}
              to={`/batch/${batchEntity.id}/force-transition`}
              replace
              color="warning"
              data-cy="entityForceTransitionButton"
            >
              <FontAwesomeIcon icon="exchange-alt" /> <span className="d-none d-md-inline">Force Transition</span>
            </Button>
          </>
        )}
      </Col>
    </Row>
  );
};

export default BatchDetail;
