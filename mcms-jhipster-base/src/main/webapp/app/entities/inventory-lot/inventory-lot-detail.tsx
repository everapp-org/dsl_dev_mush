import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './inventory-lot.reducer';

export const InventoryLotDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const inventoryLotEntity = useAppSelector(state => state.inventoryLot.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="inventoryLotDetailsHeading">Inventory Lot</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{inventoryLotEntity.id}</dd>
          <dt>
            <span id="lotCode">Lot Code</span>
            <UncontrolledTooltip target="lotCode">Internal lot identifier, e.g. &#34;LOT-2025-0042&#34;</UncontrolledTooltip>
          </dt>
          <dd>{inventoryLotEntity.lotCode}</dd>
          <dt>
            <span id="receivedDate">Received Date</span>
            <UncontrolledTooltip target="receivedDate">Date material was received into inventory</UncontrolledTooltip>
          </dt>
          <dd>
            {inventoryLotEntity.receivedDate ? (
              <TextFormat value={inventoryLotEntity.receivedDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="quantityReceived">Quantity Received</span>
            <UncontrolledTooltip target="quantityReceived">Original quantity received</UncontrolledTooltip>
          </dt>
          <dd>{inventoryLotEntity.quantityReceived}</dd>
          <dt>
            <span id="quantityOnHand">Quantity On Hand</span>
            <UncontrolledTooltip target="quantityOnHand">Current remaining quantity</UncontrolledTooltip>
          </dt>
          <dd>{inventoryLotEntity.quantityOnHand}</dd>
          <dt>
            <span id="unit">Unit</span>
            <UncontrolledTooltip target="unit">Unit of measure</UncontrolledTooltip>
          </dt>
          <dd>{inventoryLotEntity.unit}</dd>
          <dt>
            <span id="expiryDate">Expiry Date</span>
            <UncontrolledTooltip target="expiryDate">Material expiry date</UncontrolledTooltip>
          </dt>
          <dd>
            {inventoryLotEntity.expiryDate ? (
              <TextFormat value={inventoryLotEntity.expiryDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="storageLocation">Storage Location</span>
            <UncontrolledTooltip target="storageLocation">Where it&#39;s stored, e.g. &#34;Warehouse A, Shelf 3&#34;</UncontrolledTooltip>
          </dt>
          <dd>{inventoryLotEntity.storageLocation}</dd>
          <dt>
            <span id="supplierLotNumber">Supplier Lot Number</span>
            <UncontrolledTooltip target="supplierLotNumber">Supplier&#39;s lot/batch number (external reference)</UncontrolledTooltip>
          </dt>
          <dd>{inventoryLotEntity.supplierLotNumber}</dd>
          <dt>
            <span id="isExhausted">Is Exhausted</span>
            <UncontrolledTooltip target="isExhausted">True when quantityOnHand reaches 0</UncontrolledTooltip>
          </dt>
          <dd>{inventoryLotEntity.isExhausted ? 'true' : 'false'}</dd>
          <dt>
            <span id="note">Note</span>
          </dt>
          <dd>{inventoryLotEntity.note}</dd>
          <dt>Material</dt>
          <dd>
            {inventoryLotEntity.material ? (
              <Link to={`/material/${inventoryLotEntity.material.id}`}>{inventoryLotEntity.material.name}</Link>
            ) : (
              ''
            )}
          </dd>
          <dt>Supply Order Line</dt>
          <dd>
            {inventoryLotEntity.supplyOrderLine ? (
              <Link to={`/supply-order-line/${inventoryLotEntity.supplyOrderLine.id}`}>{inventoryLotEntity.supplyOrderLine.id}</Link>
            ) : (
              ''
            )}
          </dd>
        </dl>
        <Button tag={Link} to="/inventory-lot" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/inventory-lot/${inventoryLotEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default InventoryLotDetail;
