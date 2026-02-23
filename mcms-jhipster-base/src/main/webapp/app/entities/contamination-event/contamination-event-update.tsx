import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getBatches } from 'app/entities/batch/batch.reducer';
import { getEntities as getPhaseExecutions } from 'app/entities/phase-execution/phase-execution.reducer';
import { getEntities as getRooms } from 'app/entities/room/room.reducer';
import { ContaminationType } from 'app/shared/model/enumerations/contamination-type.model';
import { ContaminationSeverity } from 'app/shared/model/enumerations/contamination-severity.model';
import { ContaminationAction } from 'app/shared/model/enumerations/contamination-action.model';
import { createEntity, getEntity, reset, updateEntity } from './contamination-event.reducer';

export const ContaminationEventUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const batches = useAppSelector(state => state.batch.entities);
  const phaseExecutions = useAppSelector(state => state.phaseExecution.entities);
  const rooms = useAppSelector(state => state.room.entities);
  const contaminationEventEntity = useAppSelector(state => state.contaminationEvent.entity);
  const loading = useAppSelector(state => state.contaminationEvent.loading);
  const updating = useAppSelector(state => state.contaminationEvent.updating);
  const updateSuccess = useAppSelector(state => state.contaminationEvent.updateSuccess);
  const contaminationTypeValues = Object.keys(ContaminationType);
  const contaminationSeverityValues = Object.keys(ContaminationSeverity);
  const contaminationActionValues = Object.keys(ContaminationAction);

  const handleClose = () => {
    navigate('/contamination-event');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getBatches({}));
    dispatch(getPhaseExecutions({}));
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
    if (values.affectedBags !== undefined && typeof values.affectedBags !== 'number') {
      values.affectedBags = Number(values.affectedBags);
    }
    if (values.affectedPercentage !== undefined && typeof values.affectedPercentage !== 'number') {
      values.affectedPercentage = Number(values.affectedPercentage);
    }
    if (values.lossKg !== undefined && typeof values.lossKg !== 'number') {
      values.lossKg = Number(values.lossKg);
    }

    const entity = {
      ...contaminationEventEntity,
      ...values,
      batch: batches.find(it => it.id.toString() === values.batch?.toString()),
      phaseExecution: phaseExecutions.find(it => it.id.toString() === values.phaseExecution?.toString()),
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
          type: 'TRICHODERMA',
          severity: 'LOW',
          actionTaken: 'ISOLATE',
          ...contaminationEventEntity,
          batch: contaminationEventEntity?.batch?.id,
          phaseExecution: contaminationEventEntity?.phaseExecution?.id,
          room: contaminationEventEntity?.room?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mcmsApp.contaminationEvent.home.createOrEditLabel" data-cy="ContaminationEventCreateUpdateHeading">
            Create or edit a Contamination Event
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
                <ValidatedField name="id" required readOnly id="contamination-event-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Detected Date"
                id="contamination-event-detectedDate"
                name="detectedDate"
                data-cy="detectedDate"
                type="date"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="detectedDateLabel">When contamination was first observed</UncontrolledTooltip>
              <ValidatedField label="Type" id="contamination-event-type" name="type" data-cy="type" type="select">
                {contaminationTypeValues.map(contaminationType => (
                  <option value={contaminationType} key={contaminationType}>
                    {contaminationType}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="typeLabel">What type of contamination</UncontrolledTooltip>
              <ValidatedField label="Severity" id="contamination-event-severity" name="severity" data-cy="severity" type="select">
                {contaminationSeverityValues.map(contaminationSeverity => (
                  <option value={contaminationSeverity} key={contaminationSeverity}>
                    {contaminationSeverity}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="severityLabel">How severe</UncontrolledTooltip>
              <ValidatedField
                label="Affected Bags"
                id="contamination-event-affectedBags"
                name="affectedBags"
                data-cy="affectedBags"
                type="text"
              />
              <UncontrolledTooltip target="affectedBagsLabel">How many bags/blocks affected</UncontrolledTooltip>
              <ValidatedField
                label="Affected Percentage"
                id="contamination-event-affectedPercentage"
                name="affectedPercentage"
                data-cy="affectedPercentage"
                type="text"
              />
              <UncontrolledTooltip target="affectedPercentageLabel">% of batch affected</UncontrolledTooltip>
              <ValidatedField
                label="Action Taken"
                id="contamination-event-actionTaken"
                name="actionTaken"
                data-cy="actionTaken"
                type="select"
              >
                {contaminationActionValues.map(contaminationAction => (
                  <option value={contaminationAction} key={contaminationAction}>
                    {contaminationAction}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="actionTakenLabel">Primary response action</UncontrolledTooltip>
              <ValidatedField
                label="Resolved Date"
                id="contamination-event-resolvedDate"
                name="resolvedDate"
                data-cy="resolvedDate"
                type="date"
              />
              <UncontrolledTooltip target="resolvedDateLabel">When issue was resolved (null if ongoing)</UncontrolledTooltip>
              <ValidatedField label="Loss Kg" id="contamination-event-lossKg" name="lossKg" data-cy="lossKg" type="text" />
              <UncontrolledTooltip target="lossKgLabel">Estimated yield loss in kg</UncontrolledTooltip>
              <ValidatedField
                label="Root Cause Analysis"
                id="contamination-event-rootCauseAnalysis"
                name="rootCauseAnalysis"
                data-cy="rootCauseAnalysis"
                type="textarea"
              />
              <UncontrolledTooltip target="rootCauseAnalysisLabel">Root cause investigation notes</UncontrolledTooltip>
              <ValidatedField
                label="Preventive Measures"
                id="contamination-event-preventiveMeasures"
                name="preventiveMeasures"
                data-cy="preventiveMeasures"
                type="textarea"
              />
              <UncontrolledTooltip target="preventiveMeasuresLabel">Preventive actions for future</UncontrolledTooltip>
              <ValidatedField label="Detected By" id="contamination-event-detectedBy" name="detectedBy" data-cy="detectedBy" type="text" />
              <UncontrolledTooltip target="detectedByLabel">Who discovered it</UncontrolledTooltip>
              <ValidatedField
                label="Photos Reference"
                id="contamination-event-photosReference"
                name="photosReference"
                data-cy="photosReference"
                type="text"
              />
              <UncontrolledTooltip target="photosReferenceLabel">Path/URL to photo evidence</UncontrolledTooltip>
              <ValidatedField label="Note" id="contamination-event-note" name="note" data-cy="note" type="textarea" />
              <ValidatedField id="contamination-event-batch" name="batch" data-cy="batch" label="Batch" type="select" required>
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
                id="contamination-event-phaseExecution"
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
              <ValidatedField id="contamination-event-room" name="room" data-cy="room" label="Room" type="select">
                <option value="" key="0" />
                {rooms
                  ? rooms.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/contamination-event" replace color="info">
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

export default ContaminationEventUpdate;
