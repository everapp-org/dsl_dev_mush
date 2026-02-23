import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getSalesOrders } from 'app/entities/sales-order/sales-order.reducer';
import { getEntities as getProducts } from 'app/entities/product/product.reducer';
import { getEntities as getBatches } from 'app/entities/batch/batch.reducer';
import { QualityGrade } from 'app/shared/model/enumerations/quality-grade.model';
import { UnitOfMeasure } from 'app/shared/model/enumerations/unit-of-measure.model';
import { createEntity, getEntity, reset, updateEntity } from './sales-order-line.reducer';

export const SalesOrderLineUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const salesOrders = useAppSelector(state => state.salesOrder.entities);
  const products = useAppSelector(state => state.product.entities);
  const batches = useAppSelector(state => state.batch.entities);
  const salesOrderLineEntity = useAppSelector(state => state.salesOrderLine.entity);
  const loading = useAppSelector(state => state.salesOrderLine.loading);
  const updating = useAppSelector(state => state.salesOrderLine.updating);
  const updateSuccess = useAppSelector(state => state.salesOrderLine.updateSuccess);
  const qualityGradeValues = Object.keys(QualityGrade);
  const unitOfMeasureValues = Object.keys(UnitOfMeasure);

  const handleClose = () => {
    navigate('/sales-order-line');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getSalesOrders({}));
    dispatch(getProducts({}));
    dispatch(getBatches({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.lineNumber !== undefined && typeof values.lineNumber !== 'number') {
      values.lineNumber = Number(values.lineNumber);
    }
    if (values.weightKg !== undefined && typeof values.weightKg !== 'number') {
      values.weightKg = Number(values.weightKg);
    }
    if (values.quantityUnits !== undefined && typeof values.quantityUnits !== 'number') {
      values.quantityUnits = Number(values.quantityUnits);
    }
    if (values.pricePerKg !== undefined && typeof values.pricePerKg !== 'number') {
      values.pricePerKg = Number(values.pricePerKg);
    }
    if (values.lineTotal !== undefined && typeof values.lineTotal !== 'number') {
      values.lineTotal = Number(values.lineTotal);
    }

    const entity = {
      ...salesOrderLineEntity,
      ...values,
      salesOrder: salesOrders.find(it => it.id.toString() === values.salesOrder?.toString()),
      product: products.find(it => it.id.toString() === values.product?.toString()),
      batch: batches.find(it => it.id.toString() === values.batch?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          grade: 'A_PREMIUM',
          unit: 'KG',
          ...salesOrderLineEntity,
          salesOrder: salesOrderLineEntity?.salesOrder?.id,
          product: salesOrderLineEntity?.product?.id,
          batch: salesOrderLineEntity?.batch?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mcmsApp.salesOrderLine.home.createOrEditLabel" data-cy="SalesOrderLineCreateUpdateHeading">
            Create or edit a Sales Order Line
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField name="id" required readOnly id="sales-order-line-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Line Number"
                id="sales-order-line-lineNumber"
                name="lineNumber"
                data-cy="lineNumber"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="lineNumberLabel">Line sequence (1, 2, 3...)</UncontrolledTooltip>
              <ValidatedField
                label="Weight Kg"
                id="sales-order-line-weightKg"
                name="weightKg"
                data-cy="weightKg"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="weightKgLabel">Weight sold on this line</UncontrolledTooltip>
              <ValidatedField label="Grade" id="sales-order-line-grade" name="grade" data-cy="grade" type="select">
                {qualityGradeValues.map(qualityGrade => (
                  <option value={qualityGrade} key={qualityGrade}>
                    {qualityGrade}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="gradeLabel">Quality grade for this line</UncontrolledTooltip>
              <ValidatedField
                label="Quantity Units"
                id="sales-order-line-quantityUnits"
                name="quantityUnits"
                data-cy="quantityUnits"
                type="text"
              />
              <UncontrolledTooltip target="quantityUnitsLabel">Number of packages/units (if applicable)</UncontrolledTooltip>
              <ValidatedField label="Unit" id="sales-order-line-unit" name="unit" data-cy="unit" type="select">
                {unitOfMeasureValues.map(unitOfMeasure => (
                  <option value={unitOfMeasure} key={unitOfMeasure}>
                    {unitOfMeasure}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="unitLabel">Unit of measure</UncontrolledTooltip>
              <ValidatedField
                label="Price Per Kg"
                id="sales-order-line-pricePerKg"
                name="pricePerKg"
                data-cy="pricePerKg"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="pricePerKgLabel">Selling price per kg for this line</UncontrolledTooltip>
              <ValidatedField
                label="Line Total"
                id="sales-order-line-lineTotal"
                name="lineTotal"
                data-cy="lineTotal"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="lineTotalLabel">weightKg × pricePerKg</UncontrolledTooltip>
              <ValidatedField label="Note" id="sales-order-line-note" name="note" data-cy="note" type="textarea" />
              <ValidatedField
                id="sales-order-line-salesOrder"
                name="salesOrder"
                data-cy="salesOrder"
                label="Sales Order"
                type="select"
                required
              >
                <option value="" key="0" />
                {salesOrders
                  ? salesOrders.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.orderCode}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <ValidatedField id="sales-order-line-product" name="product" data-cy="product" label="Product" type="select" required>
                <option value="" key="0" />
                {products
                  ? products.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <ValidatedField id="sales-order-line-batch" name="batch" data-cy="batch" label="Batch" type="select">
                <option value="" key="0" />
                {batches
                  ? batches.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.batchCode}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/sales-order-line" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default SalesOrderLineUpdate;
