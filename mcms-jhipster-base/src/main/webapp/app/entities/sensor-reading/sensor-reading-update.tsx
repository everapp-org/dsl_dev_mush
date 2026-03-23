import React, { useEffect, useRef } from 'react';
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

  // Feature #362: Form ref for reset functionality
  const formRef = useRef<any>(null);
  const sensorUnitValues = Object.keys(SensorUnit);

  const handleClose = () => {
    navigate('/sensor-reading', { replace: true });
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

  // Feature #362: Reset button handler
  const handleReset = () => {
    if (formRef.current) {
      formRef.current.reset(defaultValues());
    }
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h1 id="mcmsApp.sensorReading.home.createOrEditLabel" data-cy="SensorReadingCreateUpdateHeading">
            Create or edit a Sensor Reading
          </h1>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm ref={formRef} defaultValues={defaultValues()} onSubmit={saveEntity}>
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
                  validate: {
                    number: v => isNumber(v) || 'This field should be a number.',
                    reasonableRange(v, formValues) {
                      if (!v || !isNumber(v)) return true; // Let the number check handle this
                      const numValue = Number(v);
                      const unit = formValues.unit || sensorReadingEntity?.unit || 'CELSIUS';

                      switch (unit) {
                        case 'CELSIUS':
                          if (numValue < -20 || numValue > 60) {
                            return 'Temperature must be between -20°C and 60°C';
                          }
                          break;
                        case 'HUMIDITY_PERCENT':
                          if (numValue < 0 || numValue > 100) {
                            return 'Humidity must be between 0% and 100%';
                          }
                          break;
                        case 'PPM_CO2':
                          if (numValue < 0 || numValue > 10000) {
                            return 'CO2 must be between 0 and 10000 ppm';
                          }
                          break;
                        case 'LUX':
                          if (numValue < 0 || numValue > 100000) {
                            return 'Light must be between 0 and 100000 lux';
                          }
                          break;
                        case 'AIRFLOW_MS':
                          if (numValue < 0 || numValue > 50) {
                            return 'Airflow must be between 0 and 50 m/s';
                          }
                          break;
                        default:
                          // Unknown unit, no validation
                          break;
                      }
                      return true;
                    },
                  },
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
                <option value="" key="0">
                Select sensor...
              </option>
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
                disabled={updating}
                aria-disabled={updating}
              >
                <FontAwesomeIcon icon={updating ? 'spinner' : 'save'} spin={updating} />
                &nbsp; {updating ? 'Saving...' : 'Save'}
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default SensorReadingUpdate;
