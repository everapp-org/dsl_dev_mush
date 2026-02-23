import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './material.reducer';

export const MaterialDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const materialEntity = useAppSelector(state => state.material.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="materialDetailsHeading">Material</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{materialEntity.id}</dd>
          <dt>
            <span id="code">Code</span>
            <UncontrolledTooltip target="code">Material code, e.g. &#34;MAT-SPAWN-PO-01&#34;</UncontrolledTooltip>
          </dt>
          <dd>{materialEntity.code}</dd>
          <dt>
            <span id="name">Name</span>
            <UncontrolledTooltip target="name">Display name, e.g. &#34;Pleurotus ostreatus grain spawn&#34;</UncontrolledTooltip>
          </dt>
          <dd>{materialEntity.name}</dd>
          <dt>
            <span id="category">Category</span>
            <UncontrolledTooltip target="category">Material classification</UncontrolledTooltip>
          </dt>
          <dd>{materialEntity.category}</dd>
          <dt>
            <span id="defaultUnit">Default Unit</span>
            <UncontrolledTooltip target="defaultUnit">Default unit of measure</UncontrolledTooltip>
          </dt>
          <dd>{materialEntity.defaultUnit}</dd>
          <dt>
            <span id="minimumStockLevel">Minimum Stock Level</span>
            <UncontrolledTooltip target="minimumStockLevel">Reorder threshold - alert when stock falls below</UncontrolledTooltip>
          </dt>
          <dd>{materialEntity.minimumStockLevel}</dd>
          <dt>
            <span id="description">Description</span>
            <UncontrolledTooltip target="description">Detailed description / specifications</UncontrolledTooltip>
          </dt>
          <dd>{materialEntity.description}</dd>
          <dt>
            <span id="active">Active</span>
          </dt>
          <dd>{materialEntity.active ? 'true' : 'false'}</dd>
          <dt>
            <span id="note">Note</span>
          </dt>
          <dd>{materialEntity.note}</dd>
        </dl>
        <Button tag={Link} to="/material" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/material/${materialEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default MaterialDetail;
