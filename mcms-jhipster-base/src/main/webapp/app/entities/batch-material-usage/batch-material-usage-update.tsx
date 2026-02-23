import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getBatches } from 'app/entities/batch/batch.reducer';
import { getEntities as getInventoryLots } from 'app/entities/inventory-lot/inventory-lot.reducer';
import { getEntities as getMaterials } from 'app/entities/material/material.reducer';
import { UnitOfMeasure } from 'app/shared/model/enumerations/unit-of-measure.model';
import { createEntity, getEntity, reset, updateEntity } from './batch-material-usage.reducer';

export const BatchMaterialUsageUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const batches = useAppSelector(state => state.batch.entities);
  const inventoryLots = useAppSelector(state => state.inventoryLot.entities);
  const materials = useAppSelector(state => state.material.entities);
  const batchMaterialUsageEntity = useAppSelector(state => state.batchMaterialUsage.entity);
  const loading = useAppSelector(state => state.batchMaterialUsage.loading);
  const updating = useAppSelector(state => state.batchMaterialUsage.updating);
  const updateSuccess = useAppSelector(state => state.batchMaterialUsage.updateSuccess);
  const unitOfMeasureValues = Object.keys(UnitOfMeasure);

  const handleClose = () => {
    navigate('/batch-material-usage');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getBatches({}));
    dispatch(getInventoryLots({}));
    dispatch(getMaterials({}));
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
    if (values.quantityUsed !== undefined && typeof values.quantityUsed !== 'number') {
      values.quantityUsed = Number(values.quantityUsed);
    }

    const entity = {
      ...batchMaterialUsageEntity,
      ...values,
      batch: batches.find(it => it.id.toString() === values.batch?.toString()),
      inventoryLot: inventoryLots.find(it => it.id.toString() === values.inventoryLot?.toString()),
      material: materials.find(it => it.id.toString() === values.material?.toString()),
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
          ...batchMaterialUsageEntity,
          batch: batchMaterialUsageEntity?.batch?.id,
          inventoryLot: batchMaterialUsageEntity?.inventoryLot?.id,
          material: batchMaterialUsageEntity?.material?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mcmsApp.batchMaterialUsage.home.createOrEditLabel" data-cy="BatchMaterialUsageCreateUpdateHeading">
            Create or edit a Batch Material Usage
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
                <ValidatedField name="id" required readOnly id="batch-material-usage-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Usage Date"
                id="batch-material-usage-usageDate"
                name="usageDate"
                data-cy="usageDate"
                type="date"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="usageDateLabel">When the material was used</UncontrolledTooltip>
              <ValidatedField
                label="Quantity Used"
                id="batch-material-usage-quantityUsed"
                name="quantityUsed"
                data-cy="quantityUsed"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="quantityUsedLabel">How much was consumed</UncontrolledTooltip>
              <ValidatedField label="Unit" id="batch-material-usage-unit" name="unit" data-cy="unit" type="select">
                {unitOfMeasureValues.map(unitOfMeasure => (
                  <option value={unitOfMeasure} key={unitOfMeasure}>
                    {unitOfMeasure}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="unitLabel">Unit of measure</UncontrolledTooltip>
              <ValidatedField label="Purpose" id="batch-material-usage-purpose" name="purpose" data-cy="purpose" type="text" />
              <UncontrolledTooltip target="purposeLabel">
                What it was used for, e.g. &#34;Substrate preparation&#34;, &#34;Supplementation&#34;
              </UncontrolledTooltip>
              <ValidatedField label="Note" id="batch-material-usage-note" name="note" data-cy="note" type="textarea" />
              <ValidatedField id="batch-material-usage-batch" name="batch" data-cy="batch" label="Batch" type="select" required>
                <option value="" key="0" />
                {batches
                  ? batches.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.batchCode}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <ValidatedField
                id="batch-material-usage-inventoryLot"
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
              <ValidatedField id="batch-material-usage-material" name="material" data-cy="material" label="Material" type="select" required>
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/batch-material-usage" replace color="info">
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

export default BatchMaterialUsageUpdate;
