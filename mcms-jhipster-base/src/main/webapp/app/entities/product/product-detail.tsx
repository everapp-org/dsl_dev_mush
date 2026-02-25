import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './product.reducer';

export const ProductDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const productEntity = useAppSelector(state => state.product.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="productDetailsHeading">Product</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{productEntity.id}</dd>
          <dt>
            <span id="code">Code</span>
            <UncontrolledTooltip target="code">SKU / product code, e.g. &#34;OYS-PREM-500&#34;</UncontrolledTooltip>
          </dt>
          <dd>{productEntity.code}</dd>
          <dt>
            <span id="name">Name</span>
            <UncontrolledTooltip target="name">Display name, e.g. &#34;Premium Oyster Mushroom 500g&#34;</UncontrolledTooltip>
          </dt>
          <dd>{productEntity.name}</dd>
          <dt>
            <span id="description">Description</span>
            <UncontrolledTooltip target="description">Product description</UncontrolledTooltip>
          </dt>
          <dd>{productEntity.description}</dd>
          <dt>
            <span id="defaultGrade">Default Grade</span>
            <UncontrolledTooltip target="defaultGrade">Default quality grade for this product</UncontrolledTooltip>
          </dt>
          <dd>{productEntity.defaultGrade}</dd>
          <dt>
            <span id="defaultWeightKg">Default Weight Kg</span>
            <UncontrolledTooltip target="defaultWeightKg">Standard package weight in kg</UncontrolledTooltip>
          </dt>
          <dd>{productEntity.defaultWeightKg}</dd>
          <dt>
            <span id="defaultPricePerKg">Default Price Per Kg</span>
            <UncontrolledTooltip target="defaultPricePerKg">Standard selling price per kg</UncontrolledTooltip>
          </dt>
          <dd>{productEntity.defaultPricePerKg}</dd>
          <dt>
            <span id="shelfLifeDays">Shelf Life Days</span>
            <UncontrolledTooltip target="shelfLifeDays">Product shelf life in days</UncontrolledTooltip>
          </dt>
          <dd>{productEntity.shelfLifeDays}</dd>
          <dt>
            <span id="active">Active</span>
            <UncontrolledTooltip target="active">Currently offered?</UncontrolledTooltip>
          </dt>
          <dd>{productEntity.active ? 'true' : 'false'}</dd>
          <dt>
            <span id="note">Note</span>
          </dt>
          <dd>{productEntity.note}</dd>
          <dt>Strain</dt>
          <dd>
            {productEntity.strain ? (
              <Link to={`/strain/${productEntity.strain.id}`}>{productEntity.strain.name}</Link>
            ) : (
              ''
            )}
          </dd>
        </dl>
        <Button tag={Link} to="/product" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/product/${productEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProductDetail;
