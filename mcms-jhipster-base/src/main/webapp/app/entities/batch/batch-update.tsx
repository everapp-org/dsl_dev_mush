import React, { useEffect, useRef, useState } from 'react';
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom';
import { Alert, Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { LoadingSpinner } from 'app/shared/components/LoadingSpinner';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { FormErrorSummary } from 'app/shared/error/form-error-summary';

import { getEntities as getStrains } from 'app/entities/strain/strain.reducer';
import { getEntities as getSubstrateRecipes } from 'app/entities/substrate-recipe/substrate-recipe.reducer';
import { PhaseName } from 'app/shared/model/enumerations/phase-name.model';
import { createEntity, getEntity, reset, updateEntity } from './batch.reducer';

export const BatchUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();
  const location = useLocation();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const strains = useAppSelector(state => state.strain.entities);
  const substrateRecipes = useAppSelector(state => state.substrateRecipe.entities);
  const batchEntity = useAppSelector(state => state.batch.entity);
  const loading = useAppSelector(state => state.batch.loading);
  const updating = useAppSelector(state => state.batch.updating);
  const updateSuccess = useAppSelector(state => state.batch.updateSuccess);
  const phaseNameValues = Object.keys(PhaseName);

  // Feature #362: Form ref for reset functionality
  const formRef = useRef<any>(null);

  // Track form dirty state for unsaved changes warning
  const [formIsDirty, setFormIsDirty] = useState(false);
  const [formValues, setFormValues] = useState({});
  const [showAutosaveNotice, setShowAutosaveNotice] = useState(false);
  const AUTOSAVE_KEY = isNew ? 'batch-create-autosave' : `batch-edit-autosave-${id}`;

  // Feature #370: Prevent double-click submission
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Feature #290: Get referrer search params from navigation state
  const referrerSearch = location.state?.referrerSearch || '';

  const handleClose = () => {
    // Clear autosave on intentional close
    localStorage.removeItem(AUTOSAVE_KEY);
    // Feature #290: Preserve filters when navigating back
    navigate(`/batch${referrerSearch}`, { replace: true });
  };

  const clearAutosave = () => {
    localStorage.removeItem(AUTOSAVE_KEY);
    setShowAutosaveNotice(false);
    setFormIsDirty(false);
    window.location.reload();
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
      // Try to restore autosaved data
      const savedData = localStorage.getItem(AUTOSAVE_KEY);
      if (savedData) {
        try {
          const parsed = JSON.parse(savedData);
          setFormValues(parsed);
          setFormIsDirty(true);
          setShowAutosaveNotice(true);
        } catch (e) {
          console.error('Failed to restore autosaved data', e);
          localStorage.removeItem(AUTOSAVE_KEY);
        }
      }
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getStrains({}));
    dispatch(getSubstrateRecipes({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      setFormIsDirty(false); // Clear dirty state on successful save
      localStorage.removeItem(AUTOSAVE_KEY); // Clear autosave on success
      handleClose();
    }
  }, [updateSuccess]);

  // Feature #370: Reset isSubmitting when operation completes (success or error)
  useEffect(() => {
    if (!updating) {
      setIsSubmitting(false);
    }
  }, [updating]);

  // Feature #320: Redirect to list if batch is deleted (404 error)
  useEffect(() => {
    // If editing existing batch and loading finished with no entity ID, batch was deleted
    if (!isNew && !loading && !batchEntity.id && id) {
      // Small delay to allow the error toast to be displayed
      const redirectTimer = setTimeout(() => {
        navigate('/batch', { replace: true });
      }, 1500);
      return () => clearTimeout(redirectTimer);
    }
  }, [isNew, loading, batchEntity.id, id, navigate]);

  // Warn user before leaving page with unsaved changes
  useEffect(() => {
    const handleBeforeUnload = (e: BeforeUnloadEvent) => {
      if (formIsDirty) {
        e.preventDefault();
        e.returnValue = ''; // Modern browsers show generic message
      }
    };

    window.addEventListener('beforeunload', handleBeforeUnload);
    return () => {
      window.removeEventListener('beforeunload', handleBeforeUnload);
    };
  }, [formIsDirty]);

  const saveEntity = values => {
    // Feature #370: Prevent double-click submission
    if (isSubmitting) {
      return; // Already submitting, ignore this click
    }
    setIsSubmitting(true);

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

    setFormIsDirty(false); // Clear dirty state when saving
    localStorage.removeItem(AUTOSAVE_KEY); // Clear autosave when saving
    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  // Mark form as dirty when user makes changes and autosave
  const handleFormChange = (e: any) => {
    if (!formIsDirty) {
      setFormIsDirty(true);
    }

    // Autosave to localStorage after a short delay
    if (isNew) {
      setTimeout(() => {
        const formElement = e.target.closest('form');
        if (formElement) {
          const formData = new FormData(formElement);
          const values: any = {};
          formData.forEach((value, key) => {
            // Handle checkboxes and regular inputs
            if (formElement.querySelector(`[name="${key}"][type="checkbox"]`)) {
              values[key] = formElement.querySelector(`[name="${key}"]`).checked;
            } else {
              values[key] = value;
            }
          });
          localStorage.setItem(AUTOSAVE_KEY, JSON.stringify(values));
          setFormValues(values);
        }
      }, 500);
    }
  };

  const defaultValues = () => {
    if (isNew) {
      // Feature #359, #360: Set sensible defaults for new batches
      const todayDate = new Date().toISOString().split('T')[0]; // YYYY-MM-DD format
      const newBatchDefaults = {
        isActive: true,
        startDate: todayDate,
        currentPhase: 'INOCULATION',
      };
      // Use autosaved values if available, merged with defaults
      return Object.keys(formValues).length > 0 ? { ...newBatchDefaults, ...formValues } : newBatchDefaults;
    } else {
      return {
        currentPhase: 'INOCULATION',
        ...batchEntity,
        strain: batchEntity?.strain?.id,
        recipe: batchEntity?.recipe?.id,
      };
    }
  };

  // Feature #362: Reset button handler
  const handleReset = () => {
    if (formRef.current) {
      // Clear autosave
      localStorage.removeItem(AUTOSAVE_KEY);
      setFormValues({});
      setFormIsDirty(false);
      setShowAutosaveNotice(false);
      // Reset form to default values
      formRef.current.reset(defaultValues());
    }
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h1 id="mcmsApp.batch.home.createOrEditLabel" data-cy="BatchCreateUpdateHeading">
            Create or edit a Batch
          </h1>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {showAutosaveNotice && (
            <Alert color="info" className="mb-3">
              <FontAwesomeIcon icon="info-circle" /> Your unsaved changes from a previous session have been restored.{' '}
              <Button color="link" size="sm" onClick={clearAutosave} className="p-0">
                Start fresh instead
              </Button>
            </Alert>
          )}
          {loading ? (
            <LoadingSpinner size="large" message="Loading form..." centered minHeight="200px" />
          ) : (
            <ValidatedForm ref={formRef} defaultValues={defaultValues()} onSubmit={saveEntity} onChange={handleFormChange}>
              <FormErrorSummary />
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
              <ValidatedField
                label="End Date"
                id="batch-endDate"
                name="endDate"
                data-cy="endDate"
                type="date"
                validate={{
                  validate: {
                    afterStartDate(value, formData) {
                      if (!value || !formData.startDate) return true; // Skip if either is empty
                      const startDate = new Date(formData.startDate);
                      const endDate = new Date(value);
                      return endDate > startDate || 'End date must be after start date';
                    },
                  },
                }}
              />
              <UncontrolledTooltip target="endDateLabel">Batch completion date</UncontrolledTooltip>
              <ValidatedField label="Current Phase" id="batch-currentPhase" name="currentPhase" data-cy="currentPhase" type="select">
                {phaseNameValues.map(phaseName => (
                  <option value={phaseName} key={phaseName}>
                    {phaseName}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="currentPhaseLabel">Current lifecycle phase (state machine)</UncontrolledTooltip>
              <ValidatedField
                label="Number Of Bags"
                id="batch-numberOfBags"
                name="numberOfBags"
                data-cy="numberOfBags"
                type="text"
                validate={{
                  min: { value: 1, message: 'Number of bags must be > 0' },
                }}
              />
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
              <ValidatedField
                id="batch-strain"
                name="strain"
                data-cy="strain"
                label="Strain"
                type="select"
                required
                validate={{
                  required: { value: true, message: 'Strain is required' },
                }}
              >
                <option value="" key="0">
                  Select strain...
                </option>
                {strains
                  ? strains.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="batch-recipe" name="recipe" data-cy="recipe" label="Recipe" type="select" required>
                <option value="" key="0">
                  Select recipe...
                </option>
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
              <Button color="secondary" id="reset-form" data-cy="entityResetButton" type="button" onClick={handleReset}>
                <FontAwesomeIcon icon="undo" />
                &nbsp; Reset
              </Button>
              &nbsp;
              <Button
                color="primary"
                id="save-entity"
                data-cy="entityCreateSaveButton"
                type="submit"
                disabled={updating || isSubmitting}
                aria-disabled={updating || isSubmitting}
              >
                <FontAwesomeIcon icon={updating || isSubmitting ? 'spinner' : 'save'} spin={updating || isSubmitting} />
                &nbsp; {updating || isSubmitting ? 'Saving...' : 'Save'}
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default BatchUpdate;
