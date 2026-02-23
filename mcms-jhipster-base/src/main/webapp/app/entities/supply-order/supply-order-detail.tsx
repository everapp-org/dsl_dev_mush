import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './supply-order.reducer';

export const SupplyOrderDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const supplyOrderEntity = useAppSelector(state => state.supplyOrder.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="supplyOrderDetailsHeading">Supply Order</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{supplyOrderEntity.id}</dd>
          <dt>
            <span id="orderCode">Order Code</span>
            <UncontrolledTooltip target="orderCode">PO reference number, e.g. &#34;PO-2025-042&#34;</UncontrolledTooltip>
          </dt>
          <dd>{supplyOrderEntity.orderCode}</dd>
          <dt>
            <span id="orderDate">Order Date</span>
            <UncontrolledTooltip target="orderDate">Date ordered</UncontrolledTooltip>
          </dt>
          <dd>
            {supplyOrderEntity.orderDate ? (
              <TextFormat value={supplyOrderEntity.orderDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="expectedDeliveryDate">Expected Delivery Date</span>
            <UncontrolledTooltip target="expectedDeliveryDate">Expected delivery date</UncontrolledTooltip>
          </dt>
          <dd>
            {supplyOrderEntity.expectedDeliveryDate ? (
              <TextFormat value={supplyOrderEntity.expectedDeliveryDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="actualDeliveryDate">Actual Delivery Date</span>
            <UncontrolledTooltip target="actualDeliveryDate">Actual delivery date</UncontrolledTooltip>
          </dt>
          <dd>
            {supplyOrderEntity.actualDeliveryDate ? (
              <TextFormat value={supplyOrderEntity.actualDeliveryDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="status">Status</span>
            <UncontrolledTooltip target="status">Current order status</UncontrolledTooltip>
          </dt>
          <dd>{supplyOrderEntity.status}</dd>
          <dt>
            <span id="totalAmount">Total Amount</span>
            <UncontrolledTooltip target="totalAmount">Calculated total across all lines</UncontrolledTooltip>
          </dt>
          <dd>{supplyOrderEntity.totalAmount}</dd>
          <dt>
            <span id="currency">Currency</span>
            <UncontrolledTooltip target="currency">Currency code, e.g. &#34;EUR&#34;, &#34;RSD&#34;</UncontrolledTooltip>
          </dt>
          <dd>{supplyOrderEntity.currency}</dd>
          <dt>
            <span id="shippingAddress">Shipping Address</span>
            <UncontrolledTooltip target="shippingAddress">Delivery address (if different from default)</UncontrolledTooltip>
          </dt>
          <dd>{supplyOrderEntity.shippingAddress}</dd>
          <dt>
            <span id="note">Note</span>
          </dt>
          <dd>{supplyOrderEntity.note}</dd>
          <dt>Supplier</dt>
          <dd>{supplyOrderEntity.supplier ? supplyOrderEntity.supplier.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/supply-order" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/supply-order/${supplyOrderEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default SupplyOrderDetail;
