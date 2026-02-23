import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getRooms } from 'app/entities/room/room.reducer';
import { SensorUnit } from 'app/shared/model/enumerations/sensor-unit.model';
import { SensorStatus } from 'app/shared/model/enumerations/sensor-status.model';
import { createEntity, getEntity, reset, updateEntity } from './sensor.reducer';

export const SensorUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const rooms = useAppSelector(state => state.room.entities);
  const sensorEntity = useAppSelector(state => state.sensor.entity);
  const loading = useAppSelector(state => state.sensor.loading);
  const updating = useAppSelector(state => state.sensor.updating);
  const updateSuccess = useAppSelector(state => state.sensor.updateSuccess);
  const sensorUnitValues = Object.keys(SensorUnit);
  const sensorStatusValues = Object.keys(SensorStatus);

  const handleClose = () => {
    navigate('/sensor');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

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

    const entity = {
      ...sensorEntity,
      ...values,
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
          sensorType: 'CELSIUS',
          status: 'ACTIVE',
          ...sensorEntity,
          room: sensorEntity?.room?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mcmsApp.sensor.home.createOrEditLabel" data-cy="SensorCreateUpdateHeading">
            Create or edit a Sensor
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="sensor-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Sensor Code"
                id="sensor-sensorCode"
                name="sensorCode"
                data-cy="sensorCode"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="sensorCodeLabel">Device identifier</UncontrolledTooltip>
              <ValidatedField label="Sensor Type" id="sensor-sensorType" name="sensorType" data-cy="sensorType" type="select">
                {sensorUnitValues.map(sensorUnit => (
                  <option value={sensorUnit} key={sensorUnit}>
                    {sensorUnit}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="sensorTypeLabel">What it measures</UncontrolledTooltip>
              <ValidatedField label="Status" id="sensor-status" name="status" data-cy="status" type="select">
                {sensorStatusValues.map(sensorStatus => (
                  <option value={sensorStatus} key={sensorStatus}>
                    {sensorStatus}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="statusLabel">Current operational status</UncontrolledTooltip>
              <ValidatedField label="Installed Date" id="sensor-installedDate" name="installedDate" data-cy="installedDate" type="date" />
              <UncontrolledTooltip target="installedDateLabel">Installation date</UncontrolledTooltip>
              <ValidatedField
                label="Last Calibration Date"
                id="sensor-lastCalibrationDate"
                name="lastCalibrationDate"
                data-cy="lastCalibrationDate"
                type="date"
              />
              <UncontrolledTooltip target="lastCalibrationDateLabel">Last calibration date</UncontrolledTooltip>
              <ValidatedField label="Manufacturer" id="sensor-manufacturer" name="manufacturer" data-cy="manufacturer" type="text" />
              <UncontrolledTooltip target="manufacturerLabel">Device manufacturer</UncontrolledTooltip>
              <ValidatedField label="Model" id="sensor-model" name="model" data-cy="model" type="text" />
              <UncontrolledTooltip target="modelLabel">Device model</UncontrolledTooltip>
              <ValidatedField label="Note" id="sensor-note" name="note" data-cy="note" type="textarea" />
              <ValidatedField id="sensor-room" name="room" data-cy="room" label="Room" type="select" required>
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/sensor" replace color="info">
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

export default SensorUpdate;
