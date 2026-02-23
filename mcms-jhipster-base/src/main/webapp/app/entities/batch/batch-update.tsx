import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getStrains } from 'app/entities/strain/strain.reducer';
import { getEntities as getSubstrateRecipes } from 'app/entities/substrate-recipe/substrate-recipe.reducer';
import { PhaseName } from 'app/shared/model/enumerations/phase-name.model';
import { createEntity, getEntity, reset, updateEntity } from './batch.reducer';

export const BatchUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const strains = useAppSelector(state => state.strain.entities);
  const substrateRecipes = useAppSelector(state => state.substrateRecipe.entities);
  const batchEntity = useAppSelector(state => state.batch.entity);
  const loading = useAppSelector(state => state.batch.loading);
  const updating = useAppSelector(state => state.batch.updating);
  const updateSuccess = useAppSelector(state => state.batch.updateSuccess);
  const phaseNameValues = Object.keys(PhaseName);

  const handleClose = () => {
    navigate('/batch');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getStrains({}));
    dispatch(getSubstrateRecipes({}));
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
    if (values.numberOfBags !== undefined && typeof values.numberOfBags !== 'number') {
      values.numberOfBags = Number(values.numberOfBags);
    }
    if (values.substrateWeightKg !== undefined && typeof values.substrateWeightKg !== 'number') {
      values.substrateWeightKg = Number(values.substrateWeightKg);
    }
    if (values.spawnWeightKg !== undefined && typeof values.spawnWeightKg !== 'number') {
      values.spawnWeightKg = Number(values.spawnWeightKg);
    }
    if (values.targetYieldKg !== undefined && typeof values.targetYieldKg !== 'number') {
      values.targetYieldKg = Number(values.targetYieldKg);
    }
    if (values.actualTotalYieldKg !== undefined && typeof values.actualTotalYieldKg !== 'number') {
      values.actualTotalYieldKg = Number(values.actualTotalYieldKg);
    }
    if (values.biologicalEfficiencyPercent !== undefined && typeof values.biologicalEfficiencyPercent !== 'number') {
      values.biologicalEfficiencyPercent = Number(values.biologicalEfficiencyPercent);
    }

    const entity = {
      ...batchEntity,
      ...values,
      strain: strains.find(it => it.id.toString() === values.strain?.toString()),
      recipe: substrateRecipes.find(it => it.id.toString() === values.recipe?.toString()),
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
          currentPhase: 'INOCULATION',
          ...batchEntity,
          strain: batchEntity?.strain?.id,
          recipe: batchEntity?.recipe?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mcmsApp.batch.home.createOrEditLabel" data-cy="BatchCreateUpdateHeading">
            Create or edit a Batch
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="batch-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Batch Code"
                id="batch-batchCode"
                name="batchCode"
                data-cy="batchCode"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="batchCodeLabel">Unique identifier, e.g. &#34;PO-2025-001&#34;</UncontrolledTooltip>
              <ValidatedField
                label="Start Date"
                id="batch-startDate"
                name="startDate"
                data-cy="startDate"
                type="date"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="startDateLabel">Inoculation date</UncontrolledTooltip>
              <ValidatedField label="End Date" id="batch-endDate" name="endDate" data-cy="endDate" type="date" />
              <UncontrolledTooltip target="endDateLabel">Batch completion date</UncontrolledTooltip>
              <ValidatedField label="Current Phase" id="batch-currentPhase" name="currentPhase" data-cy="currentPhase" type="select">
                {phaseNameValues.map(phaseName => (
                  <option value={phaseName} key={phaseName}>
                    {phaseName}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="currentPhaseLabel">Current lifecycle phase (state machine)</UncontrolledTooltip>
              <ValidatedField label="Number Of Bags" id="batch-numberOfBags" name="numberOfBags" data-cy="numberOfBags" type="text" />
              <UncontrolledTooltip target="numberOfBagsLabel">Total substrate bags/blocks in batch</UncontrolledTooltip>
              <ValidatedField
                label="Substrate Weight Kg"
                id="batch-substrateWeightKg"
                name="substrateWeightKg"
                data-cy="substrateWeightKg"
                type="text"
              />
              <UncontrolledTooltip target="substrateWeightKgLabel">Total substrate weight in kg</UncontrolledTooltip>
              <ValidatedField label="Spawn Weight Kg" id="batch-spawnWeightKg" name="spawnWeightKg" data-cy="spawnWeightKg" type="text" />
              <UncontrolledTooltip target="spawnWeightKgLabel">Total spawn weight used in kg</UncontrolledTooltip>
              <ValidatedField label="Target Yield Kg" id="batch-targetYieldKg" name="targetYieldKg" data-cy="targetYieldKg" type="text" />
              <UncontrolledTooltip target="targetYieldKgLabel">Expected total yield in kg</UncontrolledTooltip>
              <ValidatedField
                label="Actual Total Yield Kg"
                id="batch-actualTotalYieldKg"
                name="actualTotalYieldKg"
                data-cy="actualTotalYieldKg"
                type="text"
              />
              <UncontrolledTooltip target="actualTotalYieldKgLabel">Accumulated actual yield across all flushes</UncontrolledTooltip>
              <ValidatedField
                label="Biological Efficiency Percent"
                id="batch-biologicalEfficiencyPercent"
                name="biologicalEfficiencyPercent"
                data-cy="biologicalEfficiencyPercent"
                type="text"
              />
              <UncontrolledTooltip target="biologicalEfficiencyPercentLabel">
                (actual yield / dry substrate weight) × 100
              </UncontrolledTooltip>
              <ValidatedField
                label="Is Contaminated"
                id="batch-isContaminated"
                name="isContaminated"
                data-cy="isContaminated"
                check
                type="checkbox"
              />
              <UncontrolledTooltip target="isContaminatedLabel">Flag: any contamination detected?</UncontrolledTooltip>
              <ValidatedField label="Is Active" id="batch-isActive" name="isActive" data-cy="isActive" check type="checkbox" />
              <UncontrolledTooltip target="isActiveLabel">Batch still in production?</UncontrolledTooltip>
              <ValidatedField
                label="Completion Note"
                id="batch-completionNote"
                name="completionNote"
                data-cy="completionNote"
                type="textarea"
              />
              <UncontrolledTooltip target="completionNoteLabel">Final notes upon batch closure</UncontrolledTooltip>
              <ValidatedField label="Note" id="batch-note" name="note" data-cy="note" type="textarea" />
              <UncontrolledTooltip target="noteLabel">General batch notes</UncontrolledTooltip>
              <ValidatedField id="batch-strain" name="strain" data-cy="strain" label="Strain" type="select" required>
                <option value="" key="0" />
                {strains
                  ? strains.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <ValidatedField id="batch-recipe" name="recipe" data-cy="recipe" label="Recipe" type="select" required>
                <option value="" key="0" />
                {substrateRecipes
                  ? substrateRecipes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/batch" replace color="info">
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

export default BatchUpdate;
