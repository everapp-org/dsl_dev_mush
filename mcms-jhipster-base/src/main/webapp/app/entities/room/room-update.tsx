import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { RoomType } from 'app/shared/model/enumerations/room-type.model';
import { RoomStatus } from 'app/shared/model/enumerations/room-status.model';
import { createEntity, getEntity, reset, updateEntity } from './room.reducer';

export const RoomUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const roomEntity = useAppSelector(state => state.room.entity);
  const loading = useAppSelector(state => state.room.loading);
  const updating = useAppSelector(state => state.room.updating);
  const updateSuccess = useAppSelector(state => state.room.updateSuccess);
  const roomTypeValues = Object.keys(RoomType);
  const roomStatusValues = Object.keys(RoomStatus);

  const handleClose = () => {
    navigate('/room');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
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
    if (values.capacityBags !== undefined && typeof values.capacityBags !== 'number') {
      values.capacityBags = Number(values.capacityBags);
    }
    if (values.currentOccupancy !== undefined && typeof values.currentOccupancy !== 'number') {
      values.currentOccupancy = Number(values.currentOccupancy);
    }
    if (values.areaSqM !== undefined && typeof values.areaSqM !== 'number') {
      values.areaSqM = Number(values.areaSqM);
    }

    const entity = {
      ...roomEntity,
      ...values,
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
          roomType: 'STERILE_ZONE',
          status: 'ACTIVE',
          ...roomEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mcmsApp.room.home.createOrEditLabel" data-cy="RoomCreateUpdateHeading">
            Create or edit a Room
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="room-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Name"
                id="room-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="nameLabel">e.g. &#34;Incubation A1&#34;, &#34;Chamber B3&#34;</UncontrolledTooltip>
              <ValidatedField
                label="Code"
                id="room-code"
                name="code"
                data-cy="code"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="codeLabel">Short code, e.g. &#34;INC-A1&#34;</UncontrolledTooltip>
              <ValidatedField label="Room Type" id="room-roomType" name="roomType" data-cy="roomType" type="select">
                {roomTypeValues.map(roomType => (
                  <option value={roomType} key={roomType}>
                    {roomType}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="roomTypeLabel">Functional classification</UncontrolledTooltip>
              <ValidatedField label="Status" id="room-status" name="status" data-cy="status" type="select">
                {roomStatusValues.map(roomStatus => (
                  <option value={roomStatus} key={roomStatus}>
                    {roomStatus}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="statusLabel">Current operational status</UncontrolledTooltip>
              <ValidatedField label="Capacity Bags" id="room-capacityBags" name="capacityBags" data-cy="capacityBags" type="text" />
              <UncontrolledTooltip target="capacityBagsLabel">Max bag/block capacity</UncontrolledTooltip>
              <ValidatedField
                label="Current Occupancy"
                id="room-currentOccupancy"
                name="currentOccupancy"
                data-cy="currentOccupancy"
                type="text"
              />
              <UncontrolledTooltip target="currentOccupancyLabel">Current number of bags/blocks</UncontrolledTooltip>
              <ValidatedField label="Area Sq M" id="room-areaSqM" name="areaSqM" data-cy="areaSqM" type="text" />
              <UncontrolledTooltip target="areaSqMLabel">Floor area in m²</UncontrolledTooltip>
              <ValidatedField label="Has HVAC" id="room-hasHVAC" name="hasHVAC" data-cy="hasHVAC" check type="checkbox" />
              <UncontrolledTooltip target="hasHVACLabel">Automated climate control?</UncontrolledTooltip>
              <ValidatedField label="Has Misting" id="room-hasMisting" name="hasMisting" data-cy="hasMisting" check type="checkbox" />
              <UncontrolledTooltip target="hasMistingLabel">Automated misting/humidification?</UncontrolledTooltip>
              <ValidatedField
                label="Last Disinfection Date"
                id="room-lastDisinfectionDate"
                name="lastDisinfectionDate"
                data-cy="lastDisinfectionDate"
                type="date"
              />
              <UncontrolledTooltip target="lastDisinfectionDateLabel">When was it last disinfected?</UncontrolledTooltip>
              <ValidatedField label="Note" id="room-note" name="note" data-cy="note" type="textarea" />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/room" replace color="info">
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

export default RoomUpdate;
