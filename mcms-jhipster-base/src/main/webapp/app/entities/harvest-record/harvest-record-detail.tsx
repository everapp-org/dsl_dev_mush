import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './harvest-record.reducer';

export const HarvestRecordDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const harvestRecordEntity = useAppSelector(state => state.harvestRecord.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="harvestRecordDetailsHeading">Harvest Record</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{harvestRecordEntity.id}</dd>
          <dt>
            <span id="harvestDate">Harvest Date</span>
            <UncontrolledTooltip target="harvestDate">Date of picking</UncontrolledTooltip>
          </dt>
          <dd>
            {harvestRecordEntity.harvestDate ? (
              <TextFormat value={harvestRecordEntity.harvestDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="weightKg">Weight Kg</span>
            <UncontrolledTooltip target="weightKg">Weight harvested</UncontrolledTooltip>
          </dt>
          <dd>{harvestRecordEntity.weightKg}</dd>
          <dt>
            <span id="grade">Grade</span>
            <UncontrolledTooltip target="grade">Quality classification</UncontrolledTooltip>
          </dt>
          <dd>{harvestRecordEntity.grade}</dd>
          <dt>
            <span id="pickerName">Picker Name</span>
            <UncontrolledTooltip target="pickerName">Who harvested</UncontrolledTooltip>
          </dt>
          <dd>{harvestRecordEntity.pickerName}</dd>
          <dt>
            <span id="note">Note</span>
          </dt>
          <dd>{harvestRecordEntity.note}</dd>
          <dt>Flush Cycle</dt>
          <dd>{harvestRecordEntity.flushCycle ? harvestRecordEntity.flushCycle.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/harvest-record" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/harvest-record/${harvestRecordEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default HarvestRecordDetail;
