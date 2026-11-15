import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, Table, UncontrolledTooltip } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity, getBatchesForReport } from './monthly-report.reducer';

export const MonthlyReportDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
    dispatch(getBatchesForReport(id));
  }, []);

  const monthlyReportEntity = useAppSelector(state => state.monthlyReport.entity);
  const batchesForReport = useAppSelector(state => state.monthlyReport.batchesForReport);
  const loadingBatches = useAppSelector(state => state.monthlyReport.loadingBatches);
  return (
    <Row>
      <Col md="8">
        <h1 data-cy="monthlyReportDetailsHeading">Monthly Report</h1>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{monthlyReportEntity.id}</dd>
          <dt>
            <span id="year">Year</span>
            <UncontrolledTooltip target="year">Year</UncontrolledTooltip>
          </dt>
          <dd>{monthlyReportEntity.year}</dd>
          <dt>
            <span id="month">Month</span>
            <UncontrolledTooltip target="month">Month (1-12)</UncontrolledTooltip>
          </dt>
          <dd>{monthlyReportEntity.month}</dd>
          <dt>
            <span id="generatedAt">Generated At</span>
            <UncontrolledTooltip target="generatedAt">Report generation timestamp</UncontrolledTooltip>
          </dt>
          <dd>
            {monthlyReportEntity.generatedAt ? (
              <TextFormat value={monthlyReportEntity.generatedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="totalYieldKg">Total Yield Kg</span>
            <UncontrolledTooltip target="totalYieldKg">Total yield in reporting period</UncontrolledTooltip>
          </dt>
          <dd>{monthlyReportEntity.totalYieldKg}</dd>
          <dt>
            <span id="totalCost">Total Cost</span>
            <UncontrolledTooltip target="totalCost">Total costs in reporting period</UncontrolledTooltip>
          </dt>
          <dd>{monthlyReportEntity.totalCost}</dd>
          <dt>
            <span id="totalRevenue">Total Revenue</span>
            <UncontrolledTooltip target="totalRevenue">Total revenue in reporting period</UncontrolledTooltip>
          </dt>
          <dd>{monthlyReportEntity.totalRevenue}</dd>
          <dt>
            <span id="profitMarginPercent">Profit Margin Percent</span>
            <UncontrolledTooltip target="profitMarginPercent">Calculated profit margin</UncontrolledTooltip>
          </dt>
          <dd>{monthlyReportEntity.profitMarginPercent}</dd>
          <dt>
            <span id="totalContaminationEvents">Total Contamination Events</span>
            <UncontrolledTooltip target="totalContaminationEvents">Count of contamination events</UncontrolledTooltip>
          </dt>
          <dd>{monthlyReportEntity.totalContaminationEvents}</dd>
          <dt>
            <span id="totalMissingFields">Total Missing Fields</span>
            <UncontrolledTooltip target="totalMissingFields">Count of incomplete mandatory fields</UncontrolledTooltip>
          </dt>
          <dd>{monthlyReportEntity.totalMissingFields}</dd>
          <dt>
            <span id="summary">Summary</span>
            <UncontrolledTooltip target="summary">Executive summary text</UncontrolledTooltip>
          </dt>
          <dd>{monthlyReportEntity.summary}</dd>
          <dt>Batch</dt>
          <dd>{monthlyReportEntity.batch ? monthlyReportEntity.batch.batchCode : ''}</dd>
        </dl>
        <h4 className="mt-4">Batches Included in Report Period</h4>
        {loadingBatches ? (
          <div className="text-center">
            <FontAwesomeIcon icon="spinner" spin /> Loading batches...
          </div>
        ) : batchesForReport && batchesForReport.length > 0 ? (
          <Table responsive striped>
            <thead>
              <tr>
                <th scope="col">Batch Code</th>
                <th scope="col">Start Date</th>
                <th scope="col">Current Phase</th>
                <th scope="col">Active</th>
              </tr>
            </thead>
            <tbody>
              {batchesForReport.map(batch => (
                <tr key={batch.id}>
                  <td>
                    <Link to={`/batch/${batch.id}`}>{batch.batchCode}</Link>
                  </td>
                  <td>{batch.startDate ? <TextFormat type="date" value={batch.startDate} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{batch.currentPhase}</td>
                  <td>{batch.isActive ? 'Yes' : 'No'}</td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          <div className="alert alert-warning">No batches started in this reporting period</div>
        )}
        <Button tag={Link} to="/monthly-report" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/monthly-report/${monthlyReportEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default MonthlyReportDetail;
