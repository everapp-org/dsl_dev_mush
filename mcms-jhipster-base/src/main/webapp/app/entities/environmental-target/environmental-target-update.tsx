import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getRooms } from 'app/entities/room/room.reducer';
import { PhaseName } from 'app/shared/model/enumerations/phase-name.model';
import { createEntity, getEntity, reset, updateEntity } from './environmental-target.reducer';

export const EnvironmentalTargetUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const rooms = useAppSelector(state => state.room.entities);
  const environmentalTargetEntity = useAppSelector(state => state.environmentalTarget.entity);
  const loading = useAppSelector(state => state.environmentalTarget.loading);
  const updating = useAppSelector(state => state.environmentalTarget.updating);
  const updateSuccess = useAppSelector(state => state.environmentalTarget.updateSuccess);
  const phaseNameValues = Object.keys(PhaseName);

  const handleClose = () => {
    navigate('/environmental-target');
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
    if (values.tempMinC !== undefined && typeof values.tempMinC !== 'number') {
      values.tempMinC = Number(values.tempMinC);
    }
    if (values.tempMaxC !== undefined && typeof values.tempMaxC !== 'number') {
      values.tempMaxC = Number(values.tempMaxC);
    }
    if (values.humidityMinPercent !== undefined && typeof values.humidityMinPercent !== 'number') {
      values.humidityMinPercent = Number(values.humidityMinPercent);
    }
    if (values.humidityMaxPercent !== undefined && typeof values.humidityMaxPercent !== 'number') {
      values.humidityMaxPercent = Number(values.humidityMaxPercent);
    }
    if (values.co2MaxPpm !== undefined && typeof values.co2MaxPpm !== 'number') {
      values.co2MaxPpm = Number(values.co2MaxPpm);
    }
    if (values.lightLux !== undefined && typeof values.lightLux !== 'number') {
      values.lightLux = Number(values.lightLux);
    }
    if (values.freshAirExchangesPerHour !== undefined && typeof values.freshAirExchangesPerHour !== 'number') {
      values.freshAirExchangesPerHour = Number(values.freshAirExchangesPerHour);
    }

    const entity = {
      ...environmentalTargetEntity,
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
          phase: 'INOCULATION',
          ...environmentalTargetEntity,
          room: environmentalTargetEntity?.room?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mcmsApp.environmentalTarget.home.createOrEditLabel" data-cy="EnvironmentalTargetCreateUpdateHeading">
            Create or edit a Environmental Target
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
                <ValidatedField name="id" required readOnly id="environmental-target-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField label="Phase" id="environmental-target-phase" name="phase" data-cy="phase" type="select">
                {phaseNameValues.map(phaseName => (
                  <option value={phaseName} key={phaseName}>
                    {phaseName}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="phaseLabel">Target applies during this phase</UncontrolledTooltip>
              <ValidatedField
                label="Temp Min C"
                id="environmental-target-tempMinC"
                name="tempMinC"
                data-cy="tempMinC"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="tempMinCLabel">Minimum temperature °C</UncontrolledTooltip>
              <ValidatedField
                label="Temp Max C"
                id="environmental-target-tempMaxC"
                name="tempMaxC"
                data-cy="tempMaxC"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="tempMaxCLabel">Maximum temperature °C</UncontrolledTooltip>
              <ValidatedField
                label="Humidity Min Percent"
                id="environmental-target-humidityMinPercent"
                name="humidityMinPercent"
                data-cy="humidityMinPercent"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="humidityMinPercentLabel">Minimum RH %</UncontrolledTooltip>
              <ValidatedField
                label="Humidity Max Percent"
                id="environmental-target-humidityMaxPercent"
                name="humidityMaxPercent"
                data-cy="humidityMaxPercent"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="humidityMaxPercentLabel">Maximum RH %</UncontrolledTooltip>
              <ValidatedField label="Co 2 Max Ppm" id="environmental-target-co2MaxPpm" name="co2MaxPpm" data-cy="co2MaxPpm" type="text" />
              <UncontrolledTooltip target="co2MaxPpmLabel">Maximum CO₂ ppm</UncontrolledTooltip>
              <ValidatedField label="Light Lux" id="environmental-target-lightLux" name="lightLux" data-cy="lightLux" type="text" />
              <UncontrolledTooltip target="lightLuxLabel">Target light level</UncontrolledTooltip>
              <ValidatedField
                label="Fresh Air Exchanges Per Hour"
                id="environmental-target-freshAirExchangesPerHour"
                name="freshAirExchangesPerHour"
                data-cy="freshAirExchangesPerHour"
                type="text"
              />
              <UncontrolledTooltip target="freshAirExchangesPerHourLabel">FAE target</UncontrolledTooltip>
              <ValidatedField id="environmental-target-room" name="room" data-cy="room" label="Room" type="select" required>
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/environmental-target" replace color="info">
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

export default EnvironmentalTargetUpdate;
