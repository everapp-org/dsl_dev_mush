import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './batch-material-usage.reducer';

export const BatchMaterialUsageDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const batchMaterialUsageEntity = useAppSelector(state => state.batchMaterialUsage.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="batchMaterialUsageDetailsHeading">Batch Material Usage</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{batchMaterialUsageEntity.id}</dd>
          <dt>
            <span id="usageDate">Usage Date</span>
            <UncontrolledTooltip target="usageDate">When the material was used</UncontrolledTooltip>
          </dt>
          <dd>
            {batchMaterialUsageEntity.usageDate ? (
              <TextFormat value={batchMaterialUsageEntity.usageDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="quantityUsed">Quantity Used</span>
            <UncontrolledTooltip target="quantityUsed">How much was consumed</UncontrolledTooltip>
          </dt>
          <dd>{batchMaterialUsageEntity.quantityUsed}</dd>
          <dt>
            <span id="unit">Unit</span>
            <UncontrolledTooltip target="unit">Unit of measure</UncontrolledTooltip>
          </dt>
          <dd>{batchMaterialUsageEntity.unit}</dd>
          <dt>
            <span id="purpose">Purpose</span>
            <UncontrolledTooltip target="purpose">
              What it was used for, e.g. &#34;Substrate preparation&#34;, &#34;Supplementation&#34;
            </UncontrolledTooltip>
          </dt>
          <dd>{batchMaterialUsageEntity.purpose}</dd>
          <dt>
            <span id="note">Note</span>
          </dt>
          <dd>{batchMaterialUsageEntity.note}</dd>
          <dt>
            <span id="batch">Batch</span>
            <UncontrolledTooltip target="batch">Batch that used this material</UncontrolledTooltip>
          </dt>
          <dd>
            {batchMaterialUsageEntity.batch ? (
              <Link to={`/batch/${batchMaterialUsageEntity.batch.id}`}>{batchMaterialUsageEntity.batch.batchCode}</Link>
            ) : (
              ''
            )}
          </dd>
          <dt>
            <span id="inventoryLot">Inventory Lot</span>
            <UncontrolledTooltip target="inventoryLot">Inventory lot from which material was drawn</UncontrolledTooltip>
          </dt>
          <dd>
            {batchMaterialUsageEntity.inventoryLot ? (
              <Link to={`/inventory-lot/${batchMaterialUsageEntity.inventoryLot.id}`}>
                {batchMaterialUsageEntity.inventoryLot.lotCode}
              </Link>
            ) : (
              ''
            )}
          </dd>
          <dt>
            <span id="material">Material</span>
            <UncontrolledTooltip target="material">Type of material used</UncontrolledTooltip>
          </dt>
          <dd>
            {batchMaterialUsageEntity.material ? (
              <Link to={`/material/${batchMaterialUsageEntity.material.id}`}>{batchMaterialUsageEntity.material.name}</Link>
            ) : (
              ''
            )}
          </dd>
        </dl>
        <Button tag={Link} to="/batch-material-usage" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/batch-material-usage/${batchMaterialUsageEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default BatchMaterialUsageDetail;
