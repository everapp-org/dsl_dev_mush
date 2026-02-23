import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getRooms } from 'app/entities/room/room.reducer';
import { getEntities as getSensors } from 'app/entities/sensor/sensor.reducer';
import { getEntities as getBatches } from 'app/entities/batch/batch.reducer';
import { AlertSeverity } from 'app/shared/model/enumerations/alert-severity.model';
import { SensorUnit } from 'app/shared/model/enumerations/sensor-unit.model';
import { createEntity, getEntity, reset, updateEntity } from './environmental-alert.reducer';

export const EnvironmentalAlertUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const rooms = useAppSelector(state => state.room.entities);
  const sensors = useAppSelector(state => state.sensor.entities);
  const batches = useAppSelector(state => state.batch.entities);
  const environmentalAlertEntity = useAppSelector(state => state.environmentalAlert.entity);
  const loading = useAppSelector(state => state.environmentalAlert.loading);
  const updating = useAppSelector(state => state.environmentalAlert.updating);
  const updateSuccess = useAppSelector(state => state.environmentalAlert.updateSuccess);
  const alertSeverityValues = Object.keys(AlertSeverity);
  const sensorUnitValues = Object.keys(SensorUnit);

  const handleClose = () => {
    navigate('/environmental-alert');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getRooms({}));
    dispatch(getSensors({}));
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
    values.alertTime = convertDateTimeToServer(values.alertTime);
    if (values.actualValue !== undefined && typeof values.actualValue !== 'number') {
      values.actualValue = Number(values.actualValue);
    }
    if (values.thresholdValue !== undefined && typeof values.thresholdValue !== 'number') {
      values.thresholdValue = Number(values.thresholdValue);
    }
    values.acknowledgedAt = convertDateTimeToServer(values.acknowledgedAt);

    const entity = {
      ...environmentalAlertEntity,
      ...values,
      room: rooms.find(it => it.id.toString() === values.room?.toString()),
      sensor: sensors.find(it => it.id.toString() === values.sensor?.toString()),
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
          alertTime: displayDefaultDateTime(),
          acknowledgedAt: displayDefaultDateTime(),
        }
      : {
          severity: 'INFO',
          parameter: 'CELSIUS',
          ...environmentalAlertEntity,
          alertTime: convertDateTimeFromServer(environmentalAlertEntity.alertTime),
          acknowledgedAt: convertDateTimeFromServer(environmentalAlertEntity.acknowledgedAt),
          room: environmentalAlertEntity?.room?.id,
          sensor: environmentalAlertEntity?.sensor?.id,
          batch: environmentalAlertEntity?.batch?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mcmsApp.environmentalAlert.home.createOrEditLabel" data-cy="EnvironmentalAlertCreateUpdateHeading">
            Create or edit a Environmental Alert
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
                <ValidatedField name="id" required readOnly id="environmental-alert-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Alert Time"
                id="environmental-alert-alertTime"
                name="alertTime"
                data-cy="alertTime"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="alertTimeLabel">When the alert was triggered</UncontrolledTooltip>
              <ValidatedField label="Severity" id="environmental-alert-severity" name="severity" data-cy="severity" type="select">
                {alertSeverityValues.map(alertSeverity => (
                  <option value={alertSeverity} key={alertSeverity}>
                    {alertSeverity}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="severityLabel">INFO / WARNING / CRITICAL</UncontrolledTooltip>
              <ValidatedField label="Parameter" id="environmental-alert-parameter" name="parameter" data-cy="parameter" type="select">
                {sensorUnitValues.map(sensorUnit => (
                  <option value={sensorUnit} key={sensorUnit}>
                    {sensorUnit}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="parameterLabel">Which environmental parameter</UncontrolledTooltip>
              <ValidatedField
                label="Actual Value"
                id="environmental-alert-actualValue"
                name="actualValue"
                data-cy="actualValue"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="actualValueLabel">The value that triggered the alert</UncontrolledTooltip>
              <ValidatedField
                label="Threshold Value"
                id="environmental-alert-thresholdValue"
                name="thresholdValue"
                data-cy="thresholdValue"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="thresholdValueLabel">The threshold that was breached</UncontrolledTooltip>
              <ValidatedField
                label="Message"
                id="environmental-alert-message"
                name="message"
                data-cy="message"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="messageLabel">Human-readable alert description</UncontrolledTooltip>
              <ValidatedField
                label="Acknowledged"
                id="environmental-alert-acknowledged"
                name="acknowledged"
                data-cy="acknowledged"
                check
                type="checkbox"
              />
              <UncontrolledTooltip target="acknowledgedLabel">Has someone acknowledged this?</UncontrolledTooltip>
              <ValidatedField
                label="Acknowledged By"
                id="environmental-alert-acknowledgedBy"
                name="acknowledgedBy"
                data-cy="acknowledgedBy"
                type="text"
              />
              <UncontrolledTooltip target="acknowledgedByLabel">Who acknowledged it</UncontrolledTooltip>
              <ValidatedField
                label="Acknowledged At"
                id="environmental-alert-acknowledgedAt"
                name="acknowledgedAt"
                data-cy="acknowledgedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <UncontrolledTooltip target="acknowledgedAtLabel">When it was acknowledged</UncontrolledTooltip>
              <ValidatedField
                label="Resolution Note"
                id="environmental-alert-resolutionNote"
                name="resolutionNote"
                data-cy="resolutionNote"
                type="textarea"
              />
              <UncontrolledTooltip target="resolutionNoteLabel">What was done about it</UncontrolledTooltip>
              <ValidatedField id="environmental-alert-room" name="room" data-cy="room" label="Room" type="select" required>
                <option value="" key="0" />
                {rooms
                  ? rooms.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <ValidatedField id="environmental-alert-sensor" name="sensor" data-cy="sensor" label="Sensor" type="select">
                <option value="" key="0" />
                {sensors
                  ? sensors.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.sensorCode}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="environmental-alert-batch" name="batch" data-cy="batch" label="Batch" type="select">
                <option value="" key="0" />
                {batches
                  ? batches.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.batchCode}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/environmental-alert" replace color="info">
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

export default EnvironmentalAlertUpdate;
