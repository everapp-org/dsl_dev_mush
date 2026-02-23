import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getPhaseExecutions } from 'app/entities/phase-execution/phase-execution.reducer';
import { createEntity, getEntity, reset, updateEntity } from './mandatory-field-check.reducer';

export const MandatoryFieldCheckUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const phaseExecutions = useAppSelector(state => state.phaseExecution.entities);
  const mandatoryFieldCheckEntity = useAppSelector(state => state.mandatoryFieldCheck.entity);
  const loading = useAppSelector(state => state.mandatoryFieldCheck.loading);
  const updating = useAppSelector(state => state.mandatoryFieldCheck.updating);
  const updateSuccess = useAppSelector(state => state.mandatoryFieldCheck.updateSuccess);

  const handleClose = () => {
    navigate('/mandatory-field-check');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getPhaseExecutions({}));
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

    const entity = {
      ...mandatoryFieldCheckEntity,
      ...values,
      phaseExecution: phaseExecutions.find(it => it.id.toString() === values.phaseExecution?.toString()),
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
          ...mandatoryFieldCheckEntity,
          phaseExecution: mandatoryFieldCheckEntity?.phaseExecution?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mcmsApp.mandatoryFieldCheck.home.createOrEditLabel" data-cy="MandatoryFieldCheckCreateUpdateHeading">
            Create or edit a Mandatory Field Check
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
                <ValidatedField name="id" required readOnly id="mandatory-field-check-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Field Name"
                id="mandatory-field-check-fieldName"
                name="fieldName"
                data-cy="fieldName"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="fieldNameLabel">Which field was checked</UncontrolledTooltip>
              <ValidatedField
                label="Is Filled"
                id="mandatory-field-check-isFilled"
                name="isFilled"
                data-cy="isFilled"
                check
                type="checkbox"
              />
              <UncontrolledTooltip target="isFilledLabel">Was it filled in?</UncontrolledTooltip>
              <ValidatedField
                label="Check Date"
                id="mandatory-field-check-checkDate"
                name="checkDate"
                data-cy="checkDate"
                type="date"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="checkDateLabel">When the check was performed</UncontrolledTooltip>
              <ValidatedField
                id="mandatory-field-check-phaseExecution"
                name="phaseExecution"
                data-cy="phaseExecution"
                label="Phase Execution"
                type="select"
              >
                <option value="" key="0" />
                {phaseExecutions
                  ? phaseExecutions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/mandatory-field-check" replace color="info">
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

export default MandatoryFieldCheckUpdate;
