import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getInventoryLots } from 'app/entities/inventory-lot/inventory-lot.reducer';
import { getEntities as getBatches } from 'app/entities/batch/batch.reducer';
import { StockMovementType } from 'app/shared/model/enumerations/stock-movement-type.model';
import { UnitOfMeasure } from 'app/shared/model/enumerations/unit-of-measure.model';
import { createEntity, getEntity, reset, updateEntity } from './stock-movement.reducer';

export const StockMovementUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const inventoryLots = useAppSelector(state => state.inventoryLot.entities);
  const batches = useAppSelector(state => state.batch.entities);
  const stockMovementEntity = useAppSelector(state => state.stockMovement.entity);
  const loading = useAppSelector(state => state.stockMovement.loading);
  const updating = useAppSelector(state => state.stockMovement.updating);
  const updateSuccess = useAppSelector(state => state.stockMovement.updateSuccess);
  const stockMovementTypeValues = Object.keys(StockMovementType);
  const unitOfMeasureValues = Object.keys(UnitOfMeasure);

  const handleClose = () => {
    navigate('/stock-movement');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getInventoryLots({}));
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
    values.movementDate = convertDateTimeToServer(values.movementDate);
    if (values.quantity !== undefined && typeof values.quantity !== 'number') {
      values.quantity = Number(values.quantity);
    }

    const entity = {
      ...stockMovementEntity,
      ...values,
      inventoryLot: inventoryLots.find(it => it.id.toString() === values.inventoryLot?.toString()),
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
      ? {
          movementDate: displayDefaultDateTime(),
        }
      : {
          movementType: 'RECEIPT',
          unit: 'KG',
          ...stockMovementEntity,
          movementDate: convertDateTimeFromServer(stockMovementEntity.movementDate),
          inventoryLot: stockMovementEntity?.inventoryLot?.id,
          batch: stockMovementEntity?.batch?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mcmsApp.stockMovement.home.createOrEditLabel" data-cy="StockMovementCreateUpdateHeading">
            Create or edit a Stock Movement
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
                <ValidatedField name="id" required readOnly id="stock-movement-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Movement Date"
                id="stock-movement-movementDate"
                name="movementDate"
                data-cy="movementDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="movementDateLabel">When the movement occurred</UncontrolledTooltip>
              <ValidatedField
                label="Movement Type"
                id="stock-movement-movementType"
                name="movementType"
                data-cy="movementType"
                type="select"
              >
                {stockMovementTypeValues.map(stockMovementType => (
                  <option value={stockMovementType} key={stockMovementType}>
                    {stockMovementType}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="movementTypeLabel">RECEIPT, CONSUMPTION, ADJUSTMENT, WASTE, RETURN</UncontrolledTooltip>
              <ValidatedField
                label="Quantity"
                id="stock-movement-quantity"
                name="quantity"
                data-cy="quantity"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="quantityLabel">Quantity moved (always positive; direction from type)</UncontrolledTooltip>
              <ValidatedField label="Unit" id="stock-movement-unit" name="unit" data-cy="unit" type="select">
                {unitOfMeasureValues.map(unitOfMeasure => (
                  <option value={unitOfMeasure} key={unitOfMeasure}>
                    {unitOfMeasure}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="unitLabel">Unit of measure</UncontrolledTooltip>
              <ValidatedField label="Reference" id="stock-movement-reference" name="reference" data-cy="reference" type="text" />
              <UncontrolledTooltip target="referenceLabel">External reference (PO number, batch code, etc.)</UncontrolledTooltip>
              <ValidatedField label="Reason" id="stock-movement-reason" name="reason" data-cy="reason" type="text" />
              <UncontrolledTooltip target="reasonLabel">Reason for adjustment/waste</UncontrolledTooltip>
              <ValidatedField label="Performed By" id="stock-movement-performedBy" name="performedBy" data-cy="performedBy" type="text" />
              <UncontrolledTooltip target="performedByLabel">Who performed this transaction</UncontrolledTooltip>
              <ValidatedField label="Note" id="stock-movement-note" name="note" data-cy="note" type="textarea" />
              <ValidatedField
                id="stock-movement-inventoryLot"
                name="inventoryLot"
                data-cy="inventoryLot"
                label="Inventory Lot"
                type="select"
                required
              >
                <option value="" key="0" />
                {inventoryLots
                  ? inventoryLots.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.lotCode}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <ValidatedField id="stock-movement-batch" name="batch" data-cy="batch" label="Batch" type="select">
                <option value="" key="0" />
                {batches
                  ? batches.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.batchCode}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/stock-movement" replace color="info">
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

export default StockMovementUpdate;
