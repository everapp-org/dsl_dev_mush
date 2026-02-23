import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getFlushCycles } from 'app/entities/flush-cycle/flush-cycle.reducer';
import { QualityGrade } from 'app/shared/model/enumerations/quality-grade.model';
import { createEntity, getEntity, reset, updateEntity } from './harvest-record.reducer';

export const HarvestRecordUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const flushCycles = useAppSelector(state => state.flushCycle.entities);
  const harvestRecordEntity = useAppSelector(state => state.harvestRecord.entity);
  const loading = useAppSelector(state => state.harvestRecord.loading);
  const updating = useAppSelector(state => state.harvestRecord.updating);
  const updateSuccess = useAppSelector(state => state.harvestRecord.updateSuccess);
  const qualityGradeValues = Object.keys(QualityGrade);

  const handleClose = () => {
    navigate('/harvest-record');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getFlushCycles({}));
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
    if (values.weightKg !== undefined && typeof values.weightKg !== 'number') {
      values.weightKg = Number(values.weightKg);
    }

    const entity = {
      ...harvestRecordEntity,
      ...values,
      flushCycle: flushCycles.find(it => it.id.toString() === values.flushCycle?.toString()),
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
          ...harvestRecordEntity,
          flushCycle: harvestRecordEntity?.flushCycle?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mcmsApp.harvestRecord.home.createOrEditLabel" data-cy="HarvestRecordCreateUpdateHeading">
            Create or edit a Harvest Record
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
                <ValidatedField name="id" required readOnly id="harvest-record-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Harvest Date"
                id="harvest-record-harvestDate"
                name="harvestDate"
                data-cy="harvestDate"
                type="date"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="harvestDateLabel">Date of picking</UncontrolledTooltip>
              <ValidatedField
                label="Weight Kg"
                id="harvest-record-weightKg"
                name="weightKg"
                data-cy="weightKg"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="weightKgLabel">Weight harvested</UncontrolledTooltip>
              <ValidatedField label="Grade" id="harvest-record-grade" name="grade" data-cy="grade" type="select">
                {qualityGradeValues.map(qualityGrade => (
                  <option value={qualityGrade} key={qualityGrade}>
                    {qualityGrade}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="gradeLabel">Quality classification</UncontrolledTooltip>
              <ValidatedField label="Picker Name" id="harvest-record-pickerName" name="pickerName" data-cy="pickerName" type="text" />
              <UncontrolledTooltip target="pickerNameLabel">Who harvested</UncontrolledTooltip>
              <ValidatedField label="Note" id="harvest-record-note" name="note" data-cy="note" type="textarea" />
              <ValidatedField
                id="harvest-record-flushCycle"
                name="flushCycle"
                data-cy="flushCycle"
                label="Flush Cycle"
                type="select"
                required
              >
                <option value="" key="0" />
                {flushCycles
                  ? flushCycles.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/harvest-record" replace color="info">
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

export default HarvestRecordUpdate;
