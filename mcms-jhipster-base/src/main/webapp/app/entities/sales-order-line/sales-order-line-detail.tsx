import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './sales-order-line.reducer';

export const SalesOrderLineDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const salesOrderLineEntity = useAppSelector(state => state.salesOrderLine.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="salesOrderLineDetailsHeading">Sales Order Line</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{salesOrderLineEntity.id}</dd>
          <dt>
            <span id="lineNumber">Line Number</span>
            <UncontrolledTooltip target="lineNumber">Line sequence (1, 2, 3...)</UncontrolledTooltip>
          </dt>
          <dd>{salesOrderLineEntity.lineNumber}</dd>
          <dt>
            <span id="weightKg">Weight Kg</span>
            <UncontrolledTooltip target="weightKg">Weight sold on this line</UncontrolledTooltip>
          </dt>
          <dd>{salesOrderLineEntity.weightKg}</dd>
          <dt>
            <span id="grade">Grade</span>
            <UncontrolledTooltip target="grade">Quality grade for this line</UncontrolledTooltip>
          </dt>
          <dd>{salesOrderLineEntity.grade}</dd>
          <dt>
            <span id="quantityUnits">Quantity Units</span>
            <UncontrolledTooltip target="quantityUnits">Number of packages/units (if applicable)</UncontrolledTooltip>
          </dt>
          <dd>{salesOrderLineEntity.quantityUnits}</dd>
          <dt>
            <span id="unit">Unit</span>
            <UncontrolledTooltip target="unit">Unit of measure</UncontrolledTooltip>
          </dt>
          <dd>{salesOrderLineEntity.unit}</dd>
          <dt>
            <span id="pricePerKg">Price Per Kg</span>
            <UncontrolledTooltip target="pricePerKg">Selling price per kg for this line</UncontrolledTooltip>
          </dt>
          <dd>{salesOrderLineEntity.pricePerKg}</dd>
          <dt>
            <span id="lineTotal">Line Total</span>
            <UncontrolledTooltip target="lineTotal">weightKg × pricePerKg</UncontrolledTooltip>
          </dt>
          <dd>{salesOrderLineEntity.lineTotal}</dd>
          <dt>
            <span id="note">Note</span>
          </dt>
          <dd>{salesOrderLineEntity.note}</dd>
          <dt>Sales Order</dt>
          <dd>{salesOrderLineEntity.salesOrder ? salesOrderLineEntity.salesOrder.orderCode : ''}</dd>
          <dt>Product</dt>
          <dd>{salesOrderLineEntity.product ? salesOrderLineEntity.product.name : ''}</dd>
          <dt>Batch</dt>
          <dd>{salesOrderLineEntity.batch ? salesOrderLineEntity.batch.batchCode : ''}</dd>
        </dl>
        <Button tag={Link} to="/sales-order-line" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/sales-order-line/${salesOrderLineEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default SalesOrderLineDetail;
