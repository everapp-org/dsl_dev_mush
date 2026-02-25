import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip, Nav, NavItem, NavLink, TabContent, TabPane } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './batch.reducer';

export const BatchDetail = () => {
  const dispatch = useAppDispatch();
  const [activeTab, setActiveTab] = useState('overview');

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const batchEntity = useAppSelector(state => state.batch.entity);
  const isAdmin = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));
  const isAdminOrManager = useAppSelector(state =>
    hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN, AUTHORITIES.MANAGER]),
  );
  const canModifyBatch = useAppSelector(state =>
    hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN, AUTHORITIES.MANAGER, AUTHORITIES.OPERATOR]),
  );

  const toggleTab = (tab: string) => {
    if (activeTab !== tab) {
      setActiveTab(tab);
    }
  };

  return (
    <Row>
      <Col md="12">
        <h2 data-cy="batchDetailsHeading">
          Batch: {batchEntity.batchCode}
          <div className="d-inline ms-3">
            <Button tag={Link} to="/batch" replace color="info" data-cy="entityDetailsBackButton" size="sm">
              <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
            </Button>
            {canModifyBatch && (
              <>
                {' '}
                <Button tag={Link} to={`/batch/${batchEntity.id}/edit`} replace color="primary" size="sm">
                  <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                </Button>
              </>
            )}
            {isAdminOrManager && batchEntity.isActive && (
              <>
                {' '}
                <Button tag={Link} to={`/batch/${batchEntity.id}/discard`} replace color="danger" size="sm" data-cy="entityDiscardButton">
                  <FontAwesomeIcon icon="trash-alt" /> <span className="d-none d-md-inline">Discard</span>
                </Button>
              </>
            )}
            {isAdmin && (
              <>
                {' '}
                <Button
                  tag={Link}
                  to={`/batch/${batchEntity.id}/force-transition`}
                  replace
                  color="warning"
                  size="sm"
                  data-cy="entityForceTransitionButton"
                >
                  <FontAwesomeIcon icon="exchange-alt" /> <span className="d-none d-md-inline">Force Transition</span>
                </Button>
                {' '}
                <Button
                  tag="a"
                  href={`/api/batches/${batchEntity.id}/export-traceability`}
                  color="success"
                  size="sm"
                  data-cy="entityExportTraceabilityButton"
                >
                  <FontAwesomeIcon icon="download" /> <span className="d-none d-md-inline">Export</span>
                </Button>
              </>
            )}
          </div>
        </h2>

        <Nav tabs className="mt-3">
          <NavItem>
            <NavLink
              className={activeTab === 'overview' ? 'active' : ''}
              onClick={() => toggleTab('overview')}
              style={{ cursor: 'pointer' }}
            >
              Overview
            </NavLink>
          </NavItem>
          <NavItem>
            <NavLink
              className={activeTab === 'phaseHistory' ? 'active' : ''}
              onClick={() => toggleTab('phaseHistory')}
              style={{ cursor: 'pointer' }}
            >
              Phase History
            </NavLink>
          </NavItem>
          <NavItem>
            <NavLink
              className={activeTab === 'harvests' ? 'active' : ''}
              onClick={() => toggleTab('harvests')}
              style={{ cursor: 'pointer' }}
            >
              Harvests
            </NavLink>
          </NavItem>
          <NavItem>
            <NavLink className={activeTab === 'costs' ? 'active' : ''} onClick={() => toggleTab('costs')} style={{ cursor: 'pointer' }}>
              Costs
            </NavLink>
          </NavItem>
          <NavItem>
            <NavLink
              className={activeTab === 'materials' ? 'active' : ''}
              onClick={() => toggleTab('materials')}
              style={{ cursor: 'pointer' }}
            >
              Materials
            </NavLink>
          </NavItem>
        </Nav>

        <TabContent activeTab={activeTab} className="mt-3">
          <TabPane tabId="overview">
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
          </TabPane>

          <TabPane tabId="phaseHistory">
            <h4>Phase Execution History</h4>
            <p className="text-muted">
              <em>Phase execution records will be displayed here.</em>
            </p>
            <div className="alert alert-info">
              <FontAwesomeIcon icon="info-circle" /> Phase execution tracking shows the batch lifecycle through the 10-phase state
              machine.
            </div>
          </TabPane>

          <TabPane tabId="harvests">
            <h4>Harvest Records</h4>
            <p className="text-muted">
              <em>Harvest records for this batch will be displayed here.</em>
            </p>
            <div className="alert alert-info">
              <FontAwesomeIcon icon="info-circle" /> Track flush cycles and harvest yields across the batch lifecycle.
            </div>
          </TabPane>

          <TabPane tabId="costs">
            <h4>Cost Analysis</h4>
            <p className="text-muted">
              <em>Cost breakdown and financial analysis will be displayed here.</em>
            </p>
            <div className="alert alert-info">
              <FontAwesomeIcon icon="info-circle" /> Material costs, labor, overhead, and profitability metrics for this batch.
            </div>
          </TabPane>

          <TabPane tabId="materials">
            <h4>Materials Used</h4>
            <p className="text-muted">
              <em>Material consumption and inventory tracking will be displayed here.</em>
            </p>
            <div className="alert alert-info">
              <FontAwesomeIcon icon="info-circle" /> Substrate materials, spawn, supplements, and other consumables used in this
              batch.
            </div>
          </TabPane>
        </TabContent>
      </Col>
    </Row>
  );
};

export default BatchDetail;
