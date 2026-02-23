import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getSupplyOrders } from 'app/entities/supply-order/supply-order.reducer';
import { getEntities as getMaterials } from 'app/entities/material/material.reducer';
import { getEntities as getBatches } from 'app/entities/batch/batch.reducer';
import { UnitOfMeasure } from 'app/shared/model/enumerations/unit-of-measure.model';
import { createEntity, getEntity, reset, updateEntity } from './supply-order-line.reducer';

export const SupplyOrderLineUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const supplyOrders = useAppSelector(state => state.supplyOrder.entities);
  const materials = useAppSelector(state => state.material.entities);
  const batches = useAppSelector(state => state.batch.entities);
  const supplyOrderLineEntity = useAppSelector(state => state.supplyOrderLine.entity);
  const loading = useAppSelector(state => state.supplyOrderLine.loading);
  const updating = useAppSelector(state => state.supplyOrderLine.updating);
  const updateSuccess = useAppSelector(state => state.supplyOrderLine.updateSuccess);
  const unitOfMeasureValues = Object.keys(UnitOfMeasure);

  const handleClose = () => {
    navigate('/supply-order-line');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getSupplyOrders({}));
    dispatch(getMaterials({}));
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
    if (values.quantityOrdered !== undefined && typeof values.quantityOrdered !== 'number') {
      values.quantityOrdered = Number(values.quantityOrdered);
    }
    if (values.quantityReceived !== undefined && typeof values.quantityReceived !== 'number') {
      values.quantityReceived = Number(values.quantityReceived);
    }
    if (values.unitPrice !== undefined && typeof values.unitPrice !== 'number') {
      values.unitPrice = Number(values.unitPrice);
    }
    if (values.lineTotal !== undefined && typeof values.lineTotal !== 'number') {
      values.lineTotal = Number(values.lineTotal);
    }

    const entity = {
      ...supplyOrderLineEntity,
      ...values,
      supplyOrder: supplyOrders.find(it => it.id.toString() === values.supplyOrder?.toString()),
      material: materials.find(it => it.id.toString() === values.material?.toString()),
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
          unit: 'KG',
          ...supplyOrderLineEntity,
          supplyOrder: supplyOrderLineEntity?.supplyOrder?.id,
          material: supplyOrderLineEntity?.material?.id,
          batch: supplyOrderLineEntity?.batch?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mcmsApp.supplyOrderLine.home.createOrEditLabel" data-cy="SupplyOrderLineCreateUpdateHeading">
            Create or edit a Supply Order Line
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
                <ValidatedField name="id" required readOnly id="supply-order-line-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Line Number"
                id="supply-order-line-lineNumber"
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
                label="Item Description"
                id="supply-order-line-itemDescription"
                name="itemDescription"
                data-cy="itemDescription"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="itemDescriptionLabel">Free-text description of what is being ordered</UncontrolledTooltip>
              <ValidatedField
                label="Quantity Ordered"
                id="supply-order-line-quantityOrdered"
                name="quantityOrdered"
                data-cy="quantityOrdered"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="quantityOrderedLabel">Amount ordered</UncontrolledTooltip>
              <ValidatedField
                label="Quantity Received"
                id="supply-order-line-quantityReceived"
                name="quantityReceived"
                data-cy="quantityReceived"
                type="text"
              />
              <UncontrolledTooltip target="quantityReceivedLabel">Amount actually received (supports partial delivery)</UncontrolledTooltip>
              <ValidatedField label="Unit" id="supply-order-line-unit" name="unit" data-cy="unit" type="select">
                {unitOfMeasureValues.map(unitOfMeasure => (
                  <option value={unitOfMeasure} key={unitOfMeasure}>
                    {unitOfMeasure}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="unitLabel">Unit of measure</UncontrolledTooltip>
              <ValidatedField
                label="Unit Price"
                id="supply-order-line-unitPrice"
                name="unitPrice"
                data-cy="unitPrice"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="unitPriceLabel">Price per unit</UncontrolledTooltip>
              <ValidatedField
                label="Line Total"
                id="supply-order-line-lineTotal"
                name="lineTotal"
                data-cy="lineTotal"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="lineTotalLabel">quantityOrdered × unitPrice</UncontrolledTooltip>
              <ValidatedField label="Lot Number" id="supply-order-line-lotNumber" name="lotNumber" data-cy="lotNumber" type="text" />
              <UncontrolledTooltip target="lotNumberLabel">Supplier lot/batch number for traceability</UncontrolledTooltip>
              <ValidatedField label="Expiry Date" id="supply-order-line-expiryDate" name="expiryDate" data-cy="expiryDate" type="date" />
              <UncontrolledTooltip target="expiryDateLabel">Expiry date of supplied material</UncontrolledTooltip>
              <ValidatedField
                label="Quality On Receipt"
                id="supply-order-line-qualityOnReceipt"
                name="qualityOnReceipt"
                data-cy="qualityOnReceipt"
                type="text"
              />
              <UncontrolledTooltip target="qualityOnReceiptLabel">Quality assessment upon delivery</UncontrolledTooltip>
              <ValidatedField
                label="Is Received"
                id="supply-order-line-isReceived"
                name="isReceived"
                data-cy="isReceived"
                check
                type="checkbox"
              />
              <UncontrolledTooltip target="isReceivedLabel">Has this line been received?</UncontrolledTooltip>
              <ValidatedField label="Note" id="supply-order-line-note" name="note" data-cy="note" type="textarea" />
              <ValidatedField
                id="supply-order-line-supplyOrder"
                name="supplyOrder"
                data-cy="supplyOrder"
                label="Supply Order"
                type="select"
                required
              >
                <option value="" key="0" />
                {supplyOrders
                  ? supplyOrders.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.orderCode}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <ValidatedField id="supply-order-line-material" name="material" data-cy="material" label="Material" type="select" required>
                <option value="" key="0" />
                {materials
                  ? materials.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <ValidatedField id="supply-order-line-batch" name="batch" data-cy="batch" label="Batch" type="select">
                <option value="" key="0" />
                {batches
                  ? batches.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.batchCode}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/supply-order-line" replace color="info">
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

export default SupplyOrderLineUpdate;
