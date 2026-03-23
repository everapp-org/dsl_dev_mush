import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { LoadingSpinner } from 'app/shared/components/LoadingSpinner';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FormErrorSummary } from 'app/shared/error/form-error-summary';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { MaterialCategory } from 'app/shared/model/enumerations/material-category.model';
import { UnitOfMeasure } from 'app/shared/model/enumerations/unit-of-measure.model';
import { createEntity, getEntity, reset, updateEntity } from './material.reducer';

export const MaterialUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const materialEntity = useAppSelector(state => state.material.entity);
  const loading = useAppSelector(state => state.material.loading);
  const updating = useAppSelector(state => state.material.updating);
  const updateSuccess = useAppSelector(state => state.material.updateSuccess);
  const materialCategoryValues = Object.keys(MaterialCategory);
  const unitOfMeasureValues = Object.keys(UnitOfMeasure);

  const handleClose = () => {
    navigate('/material', { replace: true });
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
    if (values.minimumStockLevel !== undefined && typeof values.minimumStockLevel !== 'number') {
      values.minimumStockLevel = Number(values.minimumStockLevel);
    }

    const entity = {
      ...materialEntity,
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
          category: 'SPAWN',
          defaultUnit: 'KG',
          ...materialEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h1 id="mcmsApp.material.home.createOrEditLabel" data-cy="MaterialCreateUpdateHeading">
            Create or edit a Material
          </h1>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <LoadingSpinner size="large" message="Loading form..." centered minHeight="200px" />
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              <FormErrorSummary />
              {!isNew ? <ValidatedField name="id" required readOnly id="material-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Code"
                id="material-code"
                name="code"
                data-cy="code"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="codeLabel">Material code, e.g. &#34;MAT-SPAWN-PO-01&#34;</UncontrolledTooltip>
              <ValidatedField
                label="Name"
                id="material-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="nameLabel">Display name, e.g. &#34;Pleurotus ostreatus grain spawn&#34;</UncontrolledTooltip>
              <ValidatedField label="Category" id="material-category" name="category" data-cy="category" type="select">
                {materialCategoryValues.map(materialCategory => (
                  <option value={materialCategory} key={materialCategory}>
                    {materialCategory}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="categoryLabel">Material classification</UncontrolledTooltip>
              <ValidatedField label="Default Unit" id="material-defaultUnit" name="defaultUnit" data-cy="defaultUnit" type="select">
                {unitOfMeasureValues.map(unitOfMeasure => (
                  <option value={unitOfMeasure} key={unitOfMeasure}>
                    {unitOfMeasure}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="defaultUnitLabel">Default unit of measure</UncontrolledTooltip>
              <ValidatedField
                label="Minimum Stock Level"
                id="material-minimumStockLevel"
                name="minimumStockLevel"
                data-cy="minimumStockLevel"
                type="text"
              />
              <UncontrolledTooltip target="minimumStockLevelLabel">Reorder threshold - alert when stock falls below</UncontrolledTooltip>
              <ValidatedField label="Description" id="material-description" name="description" data-cy="description" type="textarea" />
              <UncontrolledTooltip target="descriptionLabel">Detailed description / specifications</UncontrolledTooltip>
              <ValidatedField label="Active" id="material-active" name="active" data-cy="active" check type="checkbox" />
              <ValidatedField label="Note" id="material-note" name="note" data-cy="note" type="textarea" />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/material" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
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

export default MaterialUpdate;
