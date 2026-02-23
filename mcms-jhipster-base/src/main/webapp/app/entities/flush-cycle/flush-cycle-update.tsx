import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getBatches } from 'app/entities/batch/batch.reducer';
import { createEntity, getEntity, reset, updateEntity } from './flush-cycle.reducer';

export const FlushCycleUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const batches = useAppSelector(state => state.batch.entities);
  const flushCycleEntity = useAppSelector(state => state.flushCycle.entity);
  const loading = useAppSelector(state => state.flushCycle.loading);
  const updating = useAppSelector(state => state.flushCycle.updating);
  const updateSuccess = useAppSelector(state => state.flushCycle.updateSuccess);

  const handleClose = () => {
    navigate('/flush-cycle');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

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
    if (values.flushNumber !== undefined && typeof values.flushNumber !== 'number') {
      values.flushNumber = Number(values.flushNumber);
    }
    if (values.yieldKg !== undefined && typeof values.yieldKg !== 'number') {
      values.yieldKg = Number(values.yieldKg);
    }
    if (values.yieldBagsHarvested !== undefined && typeof values.yieldBagsHarvested !== 'number') {
      values.yieldBagsHarvested = Number(values.yieldBagsHarvested);
    }
    if (values.avgFruitBodyWeightG !== undefined && typeof values.avgFruitBodyWeightG !== 'number') {
      values.avgFruitBodyWeightG = Number(values.avgFruitBodyWeightG);
    }
    if (values.rehydrationDurationHours !== undefined && typeof values.rehydrationDurationHours !== 'number') {
      values.rehydrationDurationHours = Number(values.rehydrationDurationHours);
    }

    const entity = {
      ...flushCycleEntity,
      ...values,
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
          ...flushCycleEntity,
          batch: flushCycleEntity?.batch?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mcmsApp.flushCycle.home.createOrEditLabel" data-cy="FlushCycleCreateUpdateHeading">
            Create or edit a Flush Cycle
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="flush-cycle-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Flush Number"
                id="flush-cycle-flushNumber"
                name="flushNumber"
                data-cy="flushNumber"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="flushNumberLabel">1st, 2nd, 3rd flush, etc.</UncontrolledTooltip>
              <ValidatedField
                label="Harvest Start Date"
                id="flush-cycle-harvestStartDate"
                name="harvestStartDate"
                data-cy="harvestStartDate"
                type="date"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="harvestStartDateLabel">When picking began</UncontrolledTooltip>
              <ValidatedField
                label="Harvest End Date"
                id="flush-cycle-harvestEndDate"
                name="harvestEndDate"
                data-cy="harvestEndDate"
                type="date"
              />
              <UncontrolledTooltip target="harvestEndDateLabel">When picking ended</UncontrolledTooltip>
              <ValidatedField
                label="Yield Kg"
                id="flush-cycle-yieldKg"
                name="yieldKg"
                data-cy="yieldKg"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="yieldKgLabel">Total yield for this flush</UncontrolledTooltip>
              <ValidatedField
                label="Yield Bags Harvested"
                id="flush-cycle-yieldBagsHarvested"
                name="yieldBagsHarvested"
                data-cy="yieldBagsHarvested"
                type="text"
              />
              <UncontrolledTooltip target="yieldBagsHarvestedLabel">Number of bags that produced</UncontrolledTooltip>
              <ValidatedField
                label="Avg Fruit Body Weight G"
                id="flush-cycle-avgFruitBodyWeightG"
                name="avgFruitBodyWeightG"
                data-cy="avgFruitBodyWeightG"
                type="text"
              />
              <UncontrolledTooltip target="avgFruitBodyWeightGLabel">Average individual mushroom weight in grams</UncontrolledTooltip>
              <ValidatedField
                label="Rehydration Done"
                id="flush-cycle-rehydrationDone"
                name="rehydrationDone"
                data-cy="rehydrationDone"
                check
                type="checkbox"
              />
              <UncontrolledTooltip target="rehydrationDoneLabel">Was rehydration performed after this flush?</UncontrolledTooltip>
              <ValidatedField
                label="Rehydration Duration Hours"
                id="flush-cycle-rehydrationDurationHours"
                name="rehydrationDurationHours"
                data-cy="rehydrationDurationHours"
                type="text"
              />
              <UncontrolledTooltip target="rehydrationDurationHoursLabel">Soaking time in hours</UncontrolledTooltip>
              <ValidatedField label="Note" id="flush-cycle-note" name="note" data-cy="note" type="textarea" />
              <UncontrolledTooltip target="noteLabel">Flush-specific observations</UncontrolledTooltip>
              <ValidatedField id="flush-cycle-batch" name="batch" data-cy="batch" label="Batch" type="select" required>
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/flush-cycle" replace color="info">
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

export default FlushCycleUpdate;
