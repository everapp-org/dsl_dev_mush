import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip, Nav, NavItem, NavLink, TabContent, TabPane, Table, Alert } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';
import axios from 'axios';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './batch.reducer';

export const BatchDetail = () => {
  const dispatch = useAppDispatch();
  const [activeTab, setActiveTab] = useState('overview');
  const [phaseExecutions, setPhaseExecutions] = useState([]);
  const [flushCycles, setFlushCycles] = useState([]);
  const [costRecords, setCostRecords] = useState([]);
  const [materialUsages, setMaterialUsages] = useState([]);
  const [contaminationEvents, setContaminationEvents] = useState([]);
  const [loadingRelated, setLoadingRelated] = useState(false);

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  useEffect(() => {
    if (id && activeTab !== 'overview') {
      loadRelatedData();
    }
  }, [id, activeTab]);

  const loadRelatedData = async () => {
    if (!id) return;
    setLoadingRelated(true);
    try {
      switch (activeTab) {
        case 'phaseHistory': {
          const phaseRes = await axios.get(`/api/phase-executions/by-batch/${id}`);
          setPhaseExecutions(phaseRes.data);
          break;
        }
        case 'harvests': {
          const flushRes = await axios.get(`/api/flush-cycles/by-batch/${id}`);
          setFlushCycles(flushRes.data);
          break;
        }
        case 'costs': {
          const costRes = await axios.get(`/api/cost-records/by-batch/${id}`);
          setCostRecords(costRes.data);
          break;
        }
        case 'materials': {
          const materialRes = await axios.get(`/api/batch-material-usages/by-batch/${id}`);
          setMaterialUsages(materialRes.data);
          break;
        }
        case 'contamination': {
          const contaminationRes = await axios.get(`/api/contamination-events/by-batch/${id}`);
          setContaminationEvents(contaminationRes.data);
          break;
        }
        default:
          break;
      }
    } catch (error) {
      console.error('Error loading related data:', error);
    } finally {
      setLoadingRelated(false);
    }
  };

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
                </Button>{' '}
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
          <NavItem>
            <NavLink
              className={activeTab === 'contamination' ? 'active' : ''}
              onClick={() => toggleTab('contamination')}
              style={{ cursor: 'pointer' }}
            >
              Contamination
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
              <dd>
                {batchEntity.startDate ? <TextFormat value={batchEntity.startDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
              </dd>
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
            {loadingRelated ? (
              <p>Loading...</p>
            ) : phaseExecutions.length > 0 ? (
              <Table striped responsive>
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Phase</th>
                    <th>Start Date</th>
                    <th>End Date</th>
                    <th>Duration (days)</th>
                    <th>Room</th>
                    <th>Responsible</th>
                  </tr>
                </thead>
                <tbody>
                  {phaseExecutions.map((phase: any, idx) => (
                    <tr key={phase.id}>
                      <td>{phase.sequenceOrder}</td>
                      <td>{phase.phase}</td>
                      <td>
                        <TextFormat value={phase.startDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
                      </td>
                      <td>{phase.endDate ? <TextFormat value={phase.endDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : '-'}</td>
                      <td>{phase.actualDurationDays || '-'}</td>
                      <td>{phase.room ? phase.room.name : '-'}</td>
                      <td>{phase.responsiblePerson || '-'}</td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            ) : (
              <Alert color="info">
                <FontAwesomeIcon icon="info-circle" /> No phase execution records found for this batch.
              </Alert>
            )}
          </TabPane>

          <TabPane tabId="harvests">
            <h4>Flush Cycles</h4>
            {loadingRelated ? (
              <p>Loading...</p>
            ) : flushCycles.length > 0 ? (
              <Table striped responsive>
                <thead>
                  <tr>
                    <th>Flush #</th>
                    <th>Harvest Start</th>
                    <th>Harvest End</th>
                    <th>Yield (kg)</th>
                    <th>Bags Harvested</th>
                    <th>Avg Fruit Weight (g)</th>
                    <th>Rehydrated</th>
                  </tr>
                </thead>
                <tbody>
                  {flushCycles.map((flush: any) => (
                    <tr key={flush.id}>
                      <td>{flush.flushNumber}</td>
                      <td>
                        {flush.harvestStartDate ? (
                          <TextFormat value={flush.harvestStartDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
                        ) : (
                          '-'
                        )}
                      </td>
                      <td>
                        {flush.harvestEndDate ? (
                          <TextFormat value={flush.harvestEndDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
                        ) : (
                          '-'
                        )}
                      </td>
                      <td>{flush.yieldKg || '-'}</td>
                      <td>{flush.yieldBagsHarvested || '-'}</td>
                      <td>{flush.avgFruitBodyWeightG || '-'}</td>
                      <td>{flush.rehydrationDone ? 'Yes' : 'No'}</td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            ) : (
              <Alert color="info">
                <FontAwesomeIcon icon="info-circle" /> No flush cycles found for this batch.
              </Alert>
            )}
          </TabPane>

          <TabPane tabId="costs">
            <h4>Cost Records</h4>
            {loadingRelated ? (
              <p>Loading...</p>
            ) : costRecords.length > 0 ? (
              <>
                <Table striped responsive>
                  <thead>
                    <tr>
                      <th>Date</th>
                      <th>Category</th>
                      <th>Description</th>
                      <th>Amount</th>
                      <th>Currency</th>
                    </tr>
                  </thead>
                  <tbody>
                    {costRecords.map((cost: any) => (
                      <tr key={cost.id}>
                        <td>
                          <TextFormat value={cost.recordDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
                        </td>
                        <td>{cost.category}</td>
                        <td>{cost.description || '-'}</td>
                        <td className="text-end">{cost.amount}</td>
                        <td>{cost.currency}</td>
                      </tr>
                    ))}
                  </tbody>
                  <tfoot>
                    <tr className="table-active">
                      <td colSpan={3} className="text-end">
                        <strong>Total:</strong>
                      </td>
                      <td className="text-end">
                        <strong>{costRecords.reduce((sum: number, cost: any) => sum + (cost.amount || 0), 0).toFixed(2)}</strong>
                      </td>
                      <td>{costRecords[0]?.currency || ''}</td>
                    </tr>
                  </tfoot>
                </Table>
              </>
            ) : (
              <Alert color="info">
                <FontAwesomeIcon icon="info-circle" /> No cost records found for this batch.
              </Alert>
            )}
          </TabPane>

          <TabPane tabId="materials">
            <h4>Material Usage</h4>
            {loadingRelated ? (
              <p>Loading...</p>
            ) : materialUsages.length > 0 ? (
              <Table striped responsive>
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Material</th>
                    <th>Quantity Used</th>
                    <th>Unit</th>
                    <th>Purpose</th>
                    <th>Lot</th>
                  </tr>
                </thead>
                <tbody>
                  {materialUsages.map((usage: any) => (
                    <tr key={usage.id}>
                      <td>
                        <TextFormat value={usage.usageDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
                      </td>
                      <td>{usage.material ? usage.material.name : '-'}</td>
                      <td>{usage.quantityUsed}</td>
                      <td>{usage.unit}</td>
                      <td>{usage.purpose || '-'}</td>
                      <td>{usage.inventoryLot ? usage.inventoryLot.lotCode : '-'}</td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            ) : (
              <Alert color="info">
                <FontAwesomeIcon icon="info-circle" /> No material usage records found for this batch.
              </Alert>
            )}
          </TabPane>

          <TabPane tabId="contamination">
            <h4>Contamination Events</h4>
            {loadingRelated ? (
              <p>Loading...</p>
            ) : contaminationEvents.length > 0 ? (
              <Table striped responsive>
                <thead>
                  <tr>
                    <th>Detected Date</th>
                    <th>Type</th>
                    <th>Severity</th>
                    <th>Affected Bags</th>
                    <th>Loss (kg)</th>
                    <th>Action Taken</th>
                    <th>Resolved</th>
                  </tr>
                </thead>
                <tbody>
                  {contaminationEvents.map((event: any) => (
                    <tr key={event.id}>
                      <td>
                        <TextFormat value={event.detectedDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
                      </td>
                      <td>{event.type}</td>
                      <td>
                        <span
                          className={`badge ${
                            event.severity === 'CRITICAL'
                              ? 'bg-danger'
                              : event.severity === 'HIGH'
                                ? 'bg-warning'
                                : event.severity === 'MEDIUM'
                                  ? 'bg-info'
                                  : 'bg-secondary'
                          }`}
                        >
                          {event.severity}
                        </span>
                      </td>
                      <td>{event.affectedBags || '-'}</td>
                      <td>{event.lossKg || '-'}</td>
                      <td>{event.actionTaken || '-'}</td>
                      <td>{event.resolvedDate ? 'Yes' : 'No'}</td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            ) : (
              <Alert color="success">
                <FontAwesomeIcon icon="check-circle" /> No contamination events recorded for this batch.
              </Alert>
            )}
          </TabPane>
        </TabContent>
      </Col>
    </Row>
  );
};

export default BatchDetail;
