import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './supply-order-line.reducer';

export const SupplyOrderLineDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const supplyOrderLineEntity = useAppSelector(state => state.supplyOrderLine.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="supplyOrderLineDetailsHeading">Supply Order Line</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{supplyOrderLineEntity.id}</dd>
          <dt>
            <span id="lineNumber">Line Number</span>
            <UncontrolledTooltip target="lineNumber">Line sequence (1, 2, 3...)</UncontrolledTooltip>
          </dt>
          <dd>{supplyOrderLineEntity.lineNumber}</dd>
          <dt>
            <span id="itemDescription">Item Description</span>
            <UncontrolledTooltip target="itemDescription">Free-text description of what is being ordered</UncontrolledTooltip>
          </dt>
          <dd>{supplyOrderLineEntity.itemDescription}</dd>
          <dt>
            <span id="quantityOrdered">Quantity Ordered</span>
            <UncontrolledTooltip target="quantityOrdered">Amount ordered</UncontrolledTooltip>
          </dt>
          <dd>{supplyOrderLineEntity.quantityOrdered}</dd>
          <dt>
            <span id="quantityReceived">Quantity Received</span>
            <UncontrolledTooltip target="quantityReceived">Amount actually received (supports partial delivery)</UncontrolledTooltip>
          </dt>
          <dd>{supplyOrderLineEntity.quantityReceived}</dd>
          <dt>
            <span id="unit">Unit</span>
            <UncontrolledTooltip target="unit">Unit of measure</UncontrolledTooltip>
          </dt>
          <dd>{supplyOrderLineEntity.unit}</dd>
          <dt>
            <span id="unitPrice">Unit Price</span>
            <UncontrolledTooltip target="unitPrice">Price per unit</UncontrolledTooltip>
          </dt>
          <dd>{supplyOrderLineEntity.unitPrice}</dd>
          <dt>
            <span id="lineTotal">Line Total</span>
            <UncontrolledTooltip target="lineTotal">quantityOrdered × unitPrice</UncontrolledTooltip>
          </dt>
          <dd>{supplyOrderLineEntity.lineTotal}</dd>
          <dt>
            <span id="lotNumber">Lot Number</span>
            <UncontrolledTooltip target="lotNumber">Supplier lot/batch number for traceability</UncontrolledTooltip>
          </dt>
          <dd>{supplyOrderLineEntity.lotNumber}</dd>
          <dt>
            <span id="expiryDate">Expiry Date</span>
            <UncontrolledTooltip target="expiryDate">Expiry date of supplied material</UncontrolledTooltip>
          </dt>
          <dd>
            {supplyOrderLineEntity.expiryDate ? (
              <TextFormat value={supplyOrderLineEntity.expiryDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="qualityOnReceipt">Quality On Receipt</span>
            <UncontrolledTooltip target="qualityOnReceipt">Quality assessment upon delivery</UncontrolledTooltip>
          </dt>
          <dd>{supplyOrderLineEntity.qualityOnReceipt}</dd>
          <dt>
            <span id="isReceived">Is Received</span>
            <UncontrolledTooltip target="isReceived">Has this line been received?</UncontrolledTooltip>
          </dt>
          <dd>{supplyOrderLineEntity.isReceived ? 'true' : 'false'}</dd>
          <dt>
            <span id="note">Note</span>
          </dt>
          <dd>{supplyOrderLineEntity.note}</dd>
          <dt>Supply Order</dt>
          <dd>{supplyOrderLineEntity.supplyOrder ? supplyOrderLineEntity.supplyOrder.orderCode : ''}</dd>
          <dt>Material</dt>
          <dd>{supplyOrderLineEntity.material ? supplyOrderLineEntity.material.name : ''}</dd>
          <dt>Batch</dt>
          <dd>{supplyOrderLineEntity.batch ? supplyOrderLineEntity.batch.batchCode : ''}</dd>
        </dl>
        <Button tag={Link} to="/supply-order-line" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/supply-order-line/${supplyOrderLineEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default SupplyOrderLineDetail;
