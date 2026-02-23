import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './customer.reducer';

export const CustomerDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const customerEntity = useAppSelector(state => state.customer.entity);
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
    </Row>
  );
};

export default CustomerDetail;
