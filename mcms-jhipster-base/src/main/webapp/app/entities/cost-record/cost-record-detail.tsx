import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './cost-record.reducer';

export const CostRecordDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const costRecordEntity = useAppSelector(state => state.costRecord.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="costRecordDetailsHeading">Cost Record</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{costRecordEntity.id}</dd>
          <dt>
            <span id="recordDate">Record Date</span>
            <UncontrolledTooltip target="recordDate">Date of expense</UncontrolledTooltip>
          </dt>
          <dd>
            {costRecordEntity.recordDate ? (
              <TextFormat value={costRecordEntity.recordDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="category">Category</span>
            <UncontrolledTooltip target="category">What type of cost</UncontrolledTooltip>
          </dt>
          <dd>{costRecordEntity.category}</dd>
          <dt>
            <span id="description">Description</span>
            <UncontrolledTooltip target="description">Brief description</UncontrolledTooltip>
          </dt>
          <dd>{costRecordEntity.description}</dd>
          <dt>
            <span id="amount">Amount</span>
            <UncontrolledTooltip target="amount">Cost amount in local currency</UncontrolledTooltip>
          </dt>
          <dd>{costRecordEntity.amount}</dd>
          <dt>
            <span id="currency">Currency</span>
            <UncontrolledTooltip target="currency">Currency code, e.g. &#34;RSD&#34;, &#34;EUR&#34;</UncontrolledTooltip>
          </dt>
          <dd>{costRecordEntity.currency}</dd>
          <dt>
            <span id="note">Note</span>
          </dt>
          <dd>{costRecordEntity.note}</dd>
          <dt>Batch</dt>
          <dd>{costRecordEntity.batch ? costRecordEntity.batch.batchCode : ''}</dd>
        </dl>
        <Button tag={Link} to="/cost-record" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/cost-record/${costRecordEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default CostRecordDetail;
