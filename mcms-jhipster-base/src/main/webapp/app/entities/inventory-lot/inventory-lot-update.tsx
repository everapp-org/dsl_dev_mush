import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getMaterials } from 'app/entities/material/material.reducer';
import { getEntities as getSupplyOrderLines } from 'app/entities/supply-order-line/supply-order-line.reducer';
import { UnitOfMeasure } from 'app/shared/model/enumerations/unit-of-measure.model';
import { createEntity, getEntity, reset, updateEntity } from './inventory-lot.reducer';

export const InventoryLotUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const materials = useAppSelector(state => state.material.entities);
  const supplyOrderLines = useAppSelector(state => state.supplyOrderLine.entities);
  const inventoryLotEntity = useAppSelector(state => state.inventoryLot.entity);
  const loading = useAppSelector(state => state.inventoryLot.loading);
  const updating = useAppSelector(state => state.inventoryLot.updating);
  const updateSuccess = useAppSelector(state => state.inventoryLot.updateSuccess);
  const unitOfMeasureValues = Object.keys(UnitOfMeasure);

  const handleClose = () => {
    navigate('/inventory-lot');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getMaterials({}));
    dispatch(getSupplyOrderLines({}));
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
    if (values.quantityReceived !== undefined && typeof values.quantityReceived !== 'number') {
      values.quantityReceived = Number(values.quantityReceived);
    }
    if (values.quantityOnHand !== undefined && typeof values.quantityOnHand !== 'number') {
      values.quantityOnHand = Number(values.quantityOnHand);
    }

    const entity = {
      ...inventoryLotEntity,
      ...values,
      material: materials.find(it => it.id.toString() === values.material?.toString()),
      supplyOrderLine: supplyOrderLines.find(it => it.id.toString() === values.supplyOrderLine?.toString()),
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
          ...inventoryLotEntity,
          material: inventoryLotEntity?.material?.id,
          supplyOrderLine: inventoryLotEntity?.supplyOrderLine?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mcmsApp.inventoryLot.home.createOrEditLabel" data-cy="InventoryLotCreateUpdateHeading">
            Create or edit a Inventory Lot
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
                <ValidatedField name="id" required readOnly id="inventory-lot-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Lot Code"
                id="inventory-lot-lotCode"
                name="lotCode"
                data-cy="lotCode"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="lotCodeLabel">Internal lot identifier, e.g. &#34;LOT-2025-0042&#34;</UncontrolledTooltip>
              <ValidatedField
                label="Received Date"
                id="inventory-lot-receivedDate"
                name="receivedDate"
                data-cy="receivedDate"
                type="date"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="receivedDateLabel">Date material was received into inventory</UncontrolledTooltip>
              <ValidatedField
                label="Quantity Received"
                id="inventory-lot-quantityReceived"
                name="quantityReceived"
                data-cy="quantityReceived"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="quantityReceivedLabel">Original quantity received</UncontrolledTooltip>
              <ValidatedField
                label="Quantity On Hand"
                id="inventory-lot-quantityOnHand"
                name="quantityOnHand"
                data-cy="quantityOnHand"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="quantityOnHandLabel">Current remaining quantity</UncontrolledTooltip>
              <ValidatedField label="Unit" id="inventory-lot-unit" name="unit" data-cy="unit" type="select">
                {unitOfMeasureValues.map(unitOfMeasure => (
                  <option value={unitOfMeasure} key={unitOfMeasure}>
                    {unitOfMeasure}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="unitLabel">Unit of measure</UncontrolledTooltip>
              <ValidatedField label="Expiry Date" id="inventory-lot-expiryDate" name="expiryDate" data-cy="expiryDate" type="date" />
              <UncontrolledTooltip target="expiryDateLabel">Material expiry date</UncontrolledTooltip>
              <ValidatedField
                label="Storage Location"
                id="inventory-lot-storageLocation"
                name="storageLocation"
                data-cy="storageLocation"
                type="text"
              />
              <UncontrolledTooltip target="storageLocationLabel">
                Where it&#39;s stored, e.g. &#34;Warehouse A, Shelf 3&#34;
              </UncontrolledTooltip>
              <ValidatedField
                label="Supplier Lot Number"
                id="inventory-lot-supplierLotNumber"
                name="supplierLotNumber"
                data-cy="supplierLotNumber"
                type="text"
              />
              <UncontrolledTooltip target="supplierLotNumberLabel">
                Supplier&#39;s lot/batch number (external reference)
              </UncontrolledTooltip>
              <ValidatedField
                label="Is Exhausted"
                id="inventory-lot-isExhausted"
                name="isExhausted"
                data-cy="isExhausted"
                check
                type="checkbox"
              />
              <UncontrolledTooltip target="isExhaustedLabel">True when quantityOnHand reaches 0</UncontrolledTooltip>
              <ValidatedField label="Note" id="inventory-lot-note" name="note" data-cy="note" type="textarea" />
              <ValidatedField id="inventory-lot-material" name="material" data-cy="material" label="Material" type="select" required>
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
              <ValidatedField
                id="inventory-lot-supplyOrderLine"
                name="supplyOrderLine"
                data-cy="supplyOrderLine"
                label="Supply Order Line"
                type="select"
                required
              >
                <option value="" key="0" />
                {supplyOrderLines
                  ? supplyOrderLines.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/inventory-lot" replace color="info">
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

export default InventoryLotUpdate;
