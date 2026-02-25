import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip, Table, Badge } from 'reactstrap';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { TextFormat } from 'react-jhipster';
import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { ISupplyOrder } from 'app/shared/model/supply-order.model';
import axios from 'axios';

import { getEntity } from './supplier.reducer';

export const SupplierDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();
  const [supplyOrders, setSupplyOrders] = useState<ISupplyOrder[]>([]);
  const [loadingOrders, setLoadingOrders] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id));

    // Fetch supply orders for this supplier
    if (id) {
      setLoadingOrders(true);
      axios
        .get<ISupplyOrder[]>(`api/suppliers/${id}/supply-orders`)
        .then(response => {
          setSupplyOrders(response.data);
          setLoadingOrders(false);
        })
        .catch(() => {
          setLoadingOrders(false);
        });
    }
  }, [id]);

  const supplierEntity = useAppSelector(state => state.supplier.entity);

  const getStatusBadgeColor = (status: string) => {
    switch (status) {
      case 'DELIVERED':
        return 'success';
      case 'ORDERED':
        return 'info';
      case 'CANCELLED':
        return 'danger';
      default:
        return 'warning';
    }
  };

  return (
    <Row>
      <Col md="8">
        <h2 data-cy="supplierDetailsHeading">Supplier</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{supplierEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
            <UncontrolledTooltip target="name">Company or individual name</UncontrolledTooltip>
          </dt>
          <dd>{supplierEntity.name}</dd>
          <dt>
            <span id="type">Type</span>
            <UncontrolledTooltip target="type">What they supply</UncontrolledTooltip>
          </dt>
          <dd>{supplierEntity.type}</dd>
          <dt>
            <span id="contactPerson">Contact Person</span>
            <UncontrolledTooltip target="contactPerson">Primary contact name</UncontrolledTooltip>
          </dt>
          <dd>{supplierEntity.contactPerson}</dd>
          <dt>
            <span id="email">Email</span>
            <UncontrolledTooltip target="email">Email</UncontrolledTooltip>
          </dt>
          <dd>{supplierEntity.email}</dd>
          <dt>
            <span id="phone">Phone</span>
            <UncontrolledTooltip target="phone">Phone number</UncontrolledTooltip>
          </dt>
          <dd>{supplierEntity.phone}</dd>
          <dt>
            <span id="address">Address</span>
            <UncontrolledTooltip target="address">Full address</UncontrolledTooltip>
          </dt>
          <dd>{supplierEntity.address}</dd>
          <dt>
            <span id="rating">Rating</span>
            <UncontrolledTooltip target="rating">Internal quality rating 1-5</UncontrolledTooltip>
          </dt>
          <dd>{supplierEntity.rating}</dd>
          <dt>
            <span id="active">Active</span>
          </dt>
          <dd>{supplierEntity.active ? 'true' : 'false'}</dd>
          <dt>
            <span id="note">Note</span>
          </dt>
          <dd>{supplierEntity.note}</dd>
        </dl>
        <Button tag={Link} to="/supplier" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/supplier/${supplierEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
        {/* Order History Section */}
        <h3 className="mt-4">Order History</h3>
        {loadingOrders ? (
          <div className="text-center">
            <div className="spinner-border" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
          </div>
        ) : supplyOrders.length === 0 ? (
          <div className="alert alert-warning">No supply orders have been placed with this supplier yet.</div>
        ) : (
          <Table responsive>
            <thead>
              <tr>
                <th>Order Code</th>
                <th>Order Date</th>
                <th>Status</th>
                <th>Total Amount</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {supplyOrders.map(order => (
                <tr key={order.id}>
                  <td>
                    <Link to={`/supply-order/${order.id}`}>{order.orderCode}</Link>
                  </td>
                  <td>
                    {order.orderDate ? (
                      <TextFormat type="date" value={order.orderDate as unknown as string} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    <Badge color={getStatusBadgeColor(order.status)}>{order.status}</Badge>
                  </td>
                  <td>{order.totalAmount != null ? `${order.totalAmount.toFixed(2)} ${order.currency}` : 'N/A'}</td>
                  <td>
                    <Button tag={Link} to={`/supply-order/${order.id}`} color="info" size="sm">
                      <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        )}
      </Col>
    </Row>
  );
};

export default SupplierDetail;
