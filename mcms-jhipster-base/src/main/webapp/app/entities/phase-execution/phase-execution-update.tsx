import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getBatches } from 'app/entities/batch/batch.reducer';
import { getEntities as getRooms } from 'app/entities/room/room.reducer';
import { PhaseName } from 'app/shared/model/enumerations/phase-name.model';
import { createEntity, getEntity, reset, updateEntity } from './phase-execution.reducer';

export const PhaseExecutionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const batches = useAppSelector(state => state.batch.entities);
  const rooms = useAppSelector(state => state.room.entities);
  const phaseExecutionEntity = useAppSelector(state => state.phaseExecution.entity);
  const loading = useAppSelector(state => state.phaseExecution.loading);
  const updating = useAppSelector(state => state.phaseExecution.updating);
  const updateSuccess = useAppSelector(state => state.phaseExecution.updateSuccess);
  const phaseNameValues = Object.keys(PhaseName);

  const handleClose = () => {
    navigate('/phase-execution');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getBatches({}));
    dispatch(getRooms({}));
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
    if (values.sequenceOrder !== undefined && typeof values.sequenceOrder !== 'number') {
      values.sequenceOrder = Number(values.sequenceOrder);
    }
    if (values.plannedDurationDays !== undefined && typeof values.plannedDurationDays !== 'number') {
      values.plannedDurationDays = Number(values.plannedDurationDays);
    }
    if (values.actualDurationDays !== undefined && typeof values.actualDurationDays !== 'number') {
      values.actualDurationDays = Number(values.actualDurationDays);
    }

    const entity = {
      ...phaseExecutionEntity,
      ...values,
      batch: batches.find(it => it.id.toString() === values.batch?.toString()),
      room: rooms.find(it => it.id.toString() === values.room?.toString()),
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
          phase: 'INOCULATION',
          ...phaseExecutionEntity,
          batch: phaseExecutionEntity?.batch?.id,
          room: phaseExecutionEntity?.room?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mcmsApp.phaseExecution.home.createOrEditLabel" data-cy="PhaseExecutionCreateUpdateHeading">
            Create or edit a Phase Execution
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
                <ValidatedField name="id" required readOnly id="phase-execution-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField label="Phase" id="phase-execution-phase" name="phase" data-cy="phase" type="select">
                {phaseNameValues.map(phaseName => (
                  <option value={phaseName} key={phaseName}>
                    {phaseName}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="phaseLabel">Which lifecycle phase</UncontrolledTooltip>
              <ValidatedField
                label="Sequence Order"
                id="phase-execution-sequenceOrder"
                name="sequenceOrder"
                data-cy="sequenceOrder"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="sequenceOrderLabel">Execution order within the batch (1, 2, 3...)</UncontrolledTooltip>
              <ValidatedField
                label="Start Date"
                id="phase-execution-startDate"
                name="startDate"
                data-cy="startDate"
                type="date"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="startDateLabel">Phase start date</UncontrolledTooltip>
              <ValidatedField label="End Date" id="phase-execution-endDate" name="endDate" data-cy="endDate" type="date" />
              <UncontrolledTooltip target="endDateLabel">Phase end date (null if ongoing)</UncontrolledTooltip>
              <ValidatedField
                label="Planned Duration Days"
                id="phase-execution-plannedDurationDays"
                name="plannedDurationDays"
                data-cy="plannedDurationDays"
                type="text"
              />
              <UncontrolledTooltip target="plannedDurationDaysLabel">Planned duration in days</UncontrolledTooltip>
              <ValidatedField
                label="Actual Duration Days"
                id="phase-execution-actualDurationDays"
                name="actualDurationDays"
                data-cy="actualDurationDays"
                type="text"
              />
              <UncontrolledTooltip target="actualDurationDaysLabel">Actual duration in days</UncontrolledTooltip>
              <ValidatedField
                label="Responsible Person"
                id="phase-execution-responsiblePerson"
                name="responsiblePerson"
                data-cy="responsiblePerson"
                type="text"
              />
              <UncontrolledTooltip target="responsiblePersonLabel">Who managed this phase (display name)</UncontrolledTooltip>
              <ValidatedField label="Note" id="phase-execution-note" name="note" data-cy="note" type="textarea" />
              <UncontrolledTooltip target="noteLabel">Phase observations &amp; notes</UncontrolledTooltip>
              <ValidatedField id="phase-execution-batch" name="batch" data-cy="batch" label="Batch" type="select" required>
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
              <ValidatedField id="phase-execution-room" name="room" data-cy="room" label="Room" type="select">
                <option value="" key="0" />
                {rooms
                  ? rooms.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/phase-execution" replace color="info">
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

export default PhaseExecutionUpdate;
