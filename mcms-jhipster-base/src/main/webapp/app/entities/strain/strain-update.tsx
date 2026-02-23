import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './strain.reducer';

export const StrainUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const strainEntity = useAppSelector(state => state.strain.entity);
  const loading = useAppSelector(state => state.strain.loading);
  const updating = useAppSelector(state => state.strain.updating);
  const updateSuccess = useAppSelector(state => state.strain.updateSuccess);

  const handleClose = () => {
    navigate('/strain');
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
    if (values.optimalTempMinC !== undefined && typeof values.optimalTempMinC !== 'number') {
      values.optimalTempMinC = Number(values.optimalTempMinC);
    }
    if (values.optimalTempMaxC !== undefined && typeof values.optimalTempMaxC !== 'number') {
      values.optimalTempMaxC = Number(values.optimalTempMaxC);
    }
    if (values.optimalHumidityMin !== undefined && typeof values.optimalHumidityMin !== 'number') {
      values.optimalHumidityMin = Number(values.optimalHumidityMin);
    }
    if (values.optimalHumidityMax !== undefined && typeof values.optimalHumidityMax !== 'number') {
      values.optimalHumidityMax = Number(values.optimalHumidityMax);
    }
    if (values.optimalCO2MaxPpm !== undefined && typeof values.optimalCO2MaxPpm !== 'number') {
      values.optimalCO2MaxPpm = Number(values.optimalCO2MaxPpm);
    }
    if (values.colonizationDaysMin !== undefined && typeof values.colonizationDaysMin !== 'number') {
      values.colonizationDaysMin = Number(values.colonizationDaysMin);
    }
    if (values.colonizationDaysMax !== undefined && typeof values.colonizationDaysMax !== 'number') {
      values.colonizationDaysMax = Number(values.colonizationDaysMax);
    }
    if (values.expectedYieldPercent !== undefined && typeof values.expectedYieldPercent !== 'number') {
      values.expectedYieldPercent = Number(values.expectedYieldPercent);
    }
    if (values.shelfLifeDays !== undefined && typeof values.shelfLifeDays !== 'number') {
      values.shelfLifeDays = Number(values.shelfLifeDays);
    }

    const entity = {
      ...strainEntity,
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
          ...strainEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mcmsApp.strain.home.createOrEditLabel" data-cy="StrainCreateUpdateHeading">
            Create or edit a Strain
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="strain-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Name"
                id="strain-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="nameLabel">e.g. &#34;Pleurotus ostreatus - Grey Dove&#34;</UncontrolledTooltip>
              <ValidatedField
                label="Species"
                id="strain-species"
                name="species"
                data-cy="species"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="speciesLabel">Latin species name</UncontrolledTooltip>
              <ValidatedField label="Variety" id="strain-variety" name="variety" data-cy="variety" type="text" />
              <UncontrolledTooltip target="varietyLabel">Cultivar or variant</UncontrolledTooltip>
              <ValidatedField
                label="Optimal Temp Min C"
                id="strain-optimalTempMinC"
                name="optimalTempMinC"
                data-cy="optimalTempMinC"
                type="text"
              />
              <UncontrolledTooltip target="optimalTempMinCLabel">Min fruiting temp °C</UncontrolledTooltip>
              <ValidatedField
                label="Optimal Temp Max C"
                id="strain-optimalTempMaxC"
                name="optimalTempMaxC"
                data-cy="optimalTempMaxC"
                type="text"
              />
              <UncontrolledTooltip target="optimalTempMaxCLabel">Max fruiting temp °C</UncontrolledTooltip>
              <ValidatedField
                label="Optimal Humidity Min"
                id="strain-optimalHumidityMin"
                name="optimalHumidityMin"
                data-cy="optimalHumidityMin"
                type="text"
              />
              <UncontrolledTooltip target="optimalHumidityMinLabel">Min RH %</UncontrolledTooltip>
              <ValidatedField
                label="Optimal Humidity Max"
                id="strain-optimalHumidityMax"
                name="optimalHumidityMax"
                data-cy="optimalHumidityMax"
                type="text"
              />
              <UncontrolledTooltip target="optimalHumidityMaxLabel">Max RH %</UncontrolledTooltip>
              <ValidatedField
                label="Optimal CO 2 Max Ppm"
                id="strain-optimalCO2MaxPpm"
                name="optimalCO2MaxPpm"
                data-cy="optimalCO2MaxPpm"
                type="text"
              />
              <UncontrolledTooltip target="optimalCO2MaxPpmLabel">Max CO₂ ppm for fruiting</UncontrolledTooltip>
              <ValidatedField
                label="Colonization Days Min"
                id="strain-colonizationDaysMin"
                name="colonizationDaysMin"
                data-cy="colonizationDaysMin"
                type="text"
              />
              <UncontrolledTooltip target="colonizationDaysMinLabel">Typical min colonization days</UncontrolledTooltip>
              <ValidatedField
                label="Colonization Days Max"
                id="strain-colonizationDaysMax"
                name="colonizationDaysMax"
                data-cy="colonizationDaysMax"
                type="text"
              />
              <UncontrolledTooltip target="colonizationDaysMaxLabel">Typical max colonization days</UncontrolledTooltip>
              <ValidatedField
                label="Expected Yield Percent"
                id="strain-expectedYieldPercent"
                name="expectedYieldPercent"
                data-cy="expectedYieldPercent"
                type="text"
              />
              <UncontrolledTooltip target="expectedYieldPercentLabel">Expected biological efficiency %</UncontrolledTooltip>
              <ValidatedField label="Shelf Life Days" id="strain-shelfLifeDays" name="shelfLifeDays" data-cy="shelfLifeDays" type="text" />
              <UncontrolledTooltip target="shelfLifeDaysLabel">Post-harvest shelf life in days</UncontrolledTooltip>
              <ValidatedField label="Note" id="strain-note" name="note" data-cy="note" type="textarea" />
              <UncontrolledTooltip target="noteLabel">Cultivation tips, observations</UncontrolledTooltip>
              <ValidatedField label="Active" id="strain-active" name="active" data-cy="active" check type="checkbox" />
              <UncontrolledTooltip target="activeLabel">Is this strain currently in use?</UncontrolledTooltip>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/strain" replace color="info">
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

export default StrainUpdate;
