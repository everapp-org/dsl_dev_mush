import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './supplier.reducer';

export const SupplierDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const supplierEntity = useAppSelector(state => state.supplier.entity);
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
      </Col>
    </Row>
  );
};

export default SupplierDetail;
