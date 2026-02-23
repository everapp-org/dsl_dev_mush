import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getSensors } from 'app/entities/sensor/sensor.reducer';
import { SensorUnit } from 'app/shared/model/enumerations/sensor-unit.model';
import { createEntity, getEntity, reset, updateEntity } from './sensor-reading.reducer';

export const SensorReadingUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const sensors = useAppSelector(state => state.sensor.entities);
  const sensorReadingEntity = useAppSelector(state => state.sensorReading.entity);
  const loading = useAppSelector(state => state.sensorReading.loading);
  const updating = useAppSelector(state => state.sensorReading.updating);
  const updateSuccess = useAppSelector(state => state.sensorReading.updateSuccess);
  const sensorUnitValues = Object.keys(SensorUnit);

  const handleClose = () => {
    navigate('/sensor-reading');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getSensors({}));
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
    values.timestamp = convertDateTimeToServer(values.timestamp);
    if (values.value !== undefined && typeof values.value !== 'number') {
      values.value = Number(values.value);
    }

    const entity = {
      ...sensorReadingEntity,
      ...values,
      sensor: sensors.find(it => it.id.toString() === values.sensor?.toString()),
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
          timestamp: displayDefaultDateTime(),
        }
      : {
          unit: 'CELSIUS',
          ...sensorReadingEntity,
          timestamp: convertDateTimeFromServer(sensorReadingEntity.timestamp),
          sensor: sensorReadingEntity?.sensor?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mcmsApp.sensorReading.home.createOrEditLabel" data-cy="SensorReadingCreateUpdateHeading">
            Create or edit a Sensor Reading
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
                <ValidatedField name="id" required readOnly id="sensor-reading-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Timestamp"
                id="sensor-reading-timestamp"
                name="timestamp"
                data-cy="timestamp"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="timestampLabel">Reading timestamp (UTC)</UncontrolledTooltip>
              <ValidatedField
                label="Value"
                id="sensor-reading-value"
                name="value"
                data-cy="value"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="valueLabel">Measured value</UncontrolledTooltip>
              <ValidatedField label="Unit" id="sensor-reading-unit" name="unit" data-cy="unit" type="select">
                {sensorUnitValues.map(sensorUnit => (
                  <option value={sensorUnit} key={sensorUnit}>
                    {sensorUnit}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="unitLabel">Unit of measurement</UncontrolledTooltip>
              <ValidatedField id="sensor-reading-sensor" name="sensor" data-cy="sensor" label="Sensor" type="select" required>
                <option value="" key="0" />
                {sensors
                  ? sensors.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.sensorCode}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/sensor-reading" replace color="info">
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

export default SensorReadingUpdate;
