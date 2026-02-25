import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip, Table, Badge } from 'reactstrap';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { TextFormat } from 'react-jhipster';
import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import axios from 'axios';
import { ISalesOrder } from 'app/shared/model/sales-order.model';

import { getEntity } from './customer.reducer';

export const CustomerDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();
  const [salesOrders, setSalesOrders] = useState<ISalesOrder[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id));
    loadSalesOrders();
  }, []);

  const loadSalesOrders = async () => {
    setLoading(true);
    try {
      const response = await axios.get<ISalesOrder[]>(`/api/customers/${id}/sales-orders`);
      setSalesOrders(response.data);
    } catch (error) {
      console.error('Error loading sales orders:', error);
    } finally {
      setLoading(false);
    }
  };

  const customerEntity = useAppSelector(state => state.customer.entity);

  const calculateTotalRevenue = () => {
    return salesOrders.reduce((sum, order) => sum + (order.totalRevenue || 0), 0);
  };
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="customerDetailsHeading">Customer</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{customerEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
            <UncontrolledTooltip target="name">Business or individual name</UncontrolledTooltip>
          </dt>
          <dd>{customerEntity.name}</dd>
          <dt>
            <span id="contactPerson">Contact Person</span>
          </dt>
          <dd>{customerEntity.contactPerson}</dd>
          <dt>
            <span id="email">Email</span>
          </dt>
          <dd>{customerEntity.email}</dd>
          <dt>
            <span id="phone">Phone</span>
          </dt>
          <dd>{customerEntity.phone}</dd>
          <dt>
            <span id="address">Address</span>
          </dt>
          <dd>{customerEntity.address}</dd>
          <dt>
            <span id="deliveryPreference">Delivery Preference</span>
            <UncontrolledTooltip target="deliveryPreference">
              e.g. &#34;Next-day cold chain&#34;, &#34;Weekly pickup&#34;
            </UncontrolledTooltip>
          </dt>
          <dd>{customerEntity.deliveryPreference}</dd>
          <dt>
            <span id="paymentTerms">Payment Terms</span>
            <UncontrolledTooltip target="paymentTerms">e.g. &#34;Net 30&#34;, &#34;COD&#34;</UncontrolledTooltip>
          </dt>
          <dd>{customerEntity.paymentTerms}</dd>
          <dt>
            <span id="active">Active</span>
          </dt>
          <dd>{customerEntity.active ? 'true' : 'false'}</dd>
          <dt>
            <span id="note">Note</span>
          </dt>
          <dd>{customerEntity.note}</dd>
        </dl>
        <Button tag={Link} to="/customer" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/customer/${customerEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
      <Col md="12" className="mt-4">
        <h3>Order History</h3>
        {loading ? (
          <div className="text-center">
            <FontAwesomeIcon icon="spinner" spin /> Loading orders...
          </div>
        ) : salesOrders.length === 0 ? (
          <div className="alert alert-info">No orders found for this customer.</div>
        ) : (
          <>
            <div className="mb-3">
              <strong>Total Revenue: </strong>
              <span className="text-success fs-5">
                {calculateTotalRevenue().toFixed(2)} {salesOrders[0]?.currency || 'USD'}
              </span>
            </div>
            <Table responsive striped>
              <thead>
                <tr>
                  <th>Order Code</th>
                  <th>Order Date</th>
                  <th>Status</th>
                  <th>Payment Status</th>
                  <th>Total Revenue</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {salesOrders.map((order, i) => (
                  <tr key={`entity-${i}`} data-cy="entityTable">
                    <td>
                      <Link to={`/sales-order/${order.id}`}>{order.orderCode}</Link>
                    </td>
                    <td>
                      {order.orderDate ? (
                        <TextFormat type="date" value={order.orderDate as unknown as string} format={APP_LOCAL_DATE_FORMAT} />
                      ) : null}
                    </td>
                    <td>
                      <Badge color={order.status === 'DELIVERED' ? 'success' : order.status === 'ORDERED' ? 'info' : 'warning'}>
                        {order.status}
                      </Badge>
                    </td>
                    <td>
                      <Badge color={order.paymentStatus === 'PAID' ? 'success' : order.paymentStatus === 'UNPAID' ? 'danger' : 'warning'}>
                        {order.paymentStatus}
                      </Badge>
                    </td>
                    <td>
                      {order.totalRevenue?.toFixed(2)} {order.currency}
                    </td>
                    <td className="text-end">
                      <div className="btn-group flex-btn-group-container">
                        <Button tag={Link} to={`/sales-order/${order.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                          <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          </>
        )}
      </Col>
    </Row>
  );
};

export default CustomerDetail;
