import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './monthly-report.reducer';

export const MonthlyReportDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const monthlyReportEntity = useAppSelector(state => state.monthlyReport.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="monthlyReportDetailsHeading">Monthly Report</h2>
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
