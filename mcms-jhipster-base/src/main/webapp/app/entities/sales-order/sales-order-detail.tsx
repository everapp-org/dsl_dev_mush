import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './sales-order.reducer';

export const SalesOrderDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const salesOrderEntity = useAppSelector(state => state.salesOrder.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="salesOrderDetailsHeading">Sales Order</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{salesOrderEntity.id}</dd>
          <dt>
            <span id="orderCode">Order Code</span>
            <UncontrolledTooltip target="orderCode">Sales order reference, e.g. &#34;SO-2025-108&#34;</UncontrolledTooltip>
          </dt>
          <dd>{salesOrderEntity.orderCode}</dd>
          <dt>
            <span id="orderDate">Order Date</span>
            <UncontrolledTooltip target="orderDate">Date order was placed</UncontrolledTooltip>
          </dt>
          <dd>
            {salesOrderEntity.orderDate ? (
              <TextFormat value={salesOrderEntity.orderDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="requestedDeliveryDate">Requested Delivery Date</span>
            <UncontrolledTooltip target="requestedDeliveryDate">Customer&#39;s requested delivery date</UncontrolledTooltip>
          </dt>
          <dd>
            {salesOrderEntity.requestedDeliveryDate ? (
              <TextFormat value={salesOrderEntity.requestedDeliveryDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="actualDeliveryDate">Actual Delivery Date</span>
            <UncontrolledTooltip target="actualDeliveryDate">Actual delivery date</UncontrolledTooltip>
          </dt>
          <dd>
            {salesOrderEntity.actualDeliveryDate ? (
              <TextFormat value={salesOrderEntity.actualDeliveryDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="status">Status</span>
            <UncontrolledTooltip target="status">Current order status</UncontrolledTooltip>
          </dt>
          <dd>{salesOrderEntity.status}</dd>
          <dt>
            <span id="totalWeight">Total Weight</span>
            <UncontrolledTooltip target="totalWeight">Calculated total weight across all lines (kg)</UncontrolledTooltip>
          </dt>
          <dd>{salesOrderEntity.totalWeight}</dd>
          <dt>
            <span id="totalRevenue">Total Revenue</span>
            <UncontrolledTooltip target="totalRevenue">Calculated total revenue across all lines</UncontrolledTooltip>
          </dt>
          <dd>{salesOrderEntity.totalRevenue}</dd>
          <dt>
            <span id="currency">Currency</span>
            <UncontrolledTooltip target="currency">Currency code</UncontrolledTooltip>
          </dt>
          <dd>{salesOrderEntity.currency}</dd>
          <dt>
            <span id="invoiceNumber">Invoice Number</span>
            <UncontrolledTooltip target="invoiceNumber">Linked invoice reference</UncontrolledTooltip>
          </dt>
          <dd>{salesOrderEntity.invoiceNumber}</dd>
          <dt>
            <span id="paymentStatus">Payment Status</span>
            <UncontrolledTooltip target="paymentStatus">Payment tracking (replaces simple isPaid boolean)</UncontrolledTooltip>
          </dt>
          <dd>{salesOrderEntity.paymentStatus}</dd>
          <dt>
            <span id="paymentDueDate">Payment Due Date</span>
            <UncontrolledTooltip target="paymentDueDate">When payment is due</UncontrolledTooltip>
          </dt>
          <dd>
            {salesOrderEntity.paymentDueDate ? (
              <TextFormat value={salesOrderEntity.paymentDueDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="paymentReceivedDate">Payment Received Date</span>
            <UncontrolledTooltip target="paymentReceivedDate">When payment was received</UncontrolledTooltip>
          </dt>
          <dd>
            {salesOrderEntity.paymentReceivedDate ? (
              <TextFormat value={salesOrderEntity.paymentReceivedDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="shippingAddress">Shipping Address</span>
            <UncontrolledTooltip target="shippingAddress">Delivery address</UncontrolledTooltip>
          </dt>
          <dd>{salesOrderEntity.shippingAddress}</dd>
          <dt>
            <span id="note">Note</span>
          </dt>
          <dd>{salesOrderEntity.note}</dd>
          <dt>Customer</dt>
          <dd>{salesOrderEntity.customer ? salesOrderEntity.customer.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/sales-order" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/sales-order/${salesOrderEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default SalesOrderDetail;
