import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getStrains } from 'app/entities/strain/strain.reducer';
import { QualityGrade } from 'app/shared/model/enumerations/quality-grade.model';
import { createEntity, getEntity, reset, updateEntity } from './product.reducer';

export const ProductUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const strains = useAppSelector(state => state.strain.entities);
  const productEntity = useAppSelector(state => state.product.entity);
  const loading = useAppSelector(state => state.product.loading);
  const updating = useAppSelector(state => state.product.updating);
  const updateSuccess = useAppSelector(state => state.product.updateSuccess);
  const qualityGradeValues = Object.keys(QualityGrade);

  const handleClose = () => {
    navigate('/product');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getStrains({}));
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
    if (values.defaultWeightKg !== undefined && typeof values.defaultWeightKg !== 'number') {
      values.defaultWeightKg = Number(values.defaultWeightKg);
    }
    if (values.defaultPricePerKg !== undefined && typeof values.defaultPricePerKg !== 'number') {
      values.defaultPricePerKg = Number(values.defaultPricePerKg);
    }
    if (values.shelfLifeDays !== undefined && typeof values.shelfLifeDays !== 'number') {
      values.shelfLifeDays = Number(values.shelfLifeDays);
    }

    const entity = {
      ...productEntity,
      ...values,
      strain: strains.find(it => it.id.toString() === values.strain?.toString()),
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
          defaultGrade: 'A_PREMIUM',
          ...productEntity,
          strain: productEntity?.strain?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mcmsApp.product.home.createOrEditLabel" data-cy="ProductCreateUpdateHeading">
            Create or edit a Product
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="product-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Code"
                id="product-code"
                name="code"
                data-cy="code"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="codeLabel">SKU / product code, e.g. &#34;OYS-PREM-500&#34;</UncontrolledTooltip>
              <ValidatedField
                label="Name"
                id="product-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="nameLabel">Display name, e.g. &#34;Premium Oyster Mushroom 500g&#34;</UncontrolledTooltip>
              <ValidatedField label="Description" id="product-description" name="description" data-cy="description" type="textarea" />
              <UncontrolledTooltip target="descriptionLabel">Product description</UncontrolledTooltip>
              <ValidatedField label="Default Grade" id="product-defaultGrade" name="defaultGrade" data-cy="defaultGrade" type="select">
                {qualityGradeValues.map(qualityGrade => (
                  <option value={qualityGrade} key={qualityGrade}>
                    {qualityGrade}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="defaultGradeLabel">Default quality grade for this product</UncontrolledTooltip>
              <ValidatedField
                label="Default Weight Kg"
                id="product-defaultWeightKg"
                name="defaultWeightKg"
                data-cy="defaultWeightKg"
                type="text"
              />
              <UncontrolledTooltip target="defaultWeightKgLabel">Standard package weight in kg</UncontrolledTooltip>
              <ValidatedField
                label="Default Price Per Kg"
                id="product-defaultPricePerKg"
                name="defaultPricePerKg"
                data-cy="defaultPricePerKg"
                type="text"
              />
              <UncontrolledTooltip target="defaultPricePerKgLabel">Standard selling price per kg</UncontrolledTooltip>
              <ValidatedField label="Shelf Life Days" id="product-shelfLifeDays" name="shelfLifeDays" data-cy="shelfLifeDays" type="text" />
              <UncontrolledTooltip target="shelfLifeDaysLabel">Product shelf life in days</UncontrolledTooltip>
              <ValidatedField label="Active" id="product-active" name="active" data-cy="active" check type="checkbox" />
              <UncontrolledTooltip target="activeLabel">Currently offered?</UncontrolledTooltip>
              <ValidatedField label="Note" id="product-note" name="note" data-cy="note" type="textarea" />
              <ValidatedField id="product-strain" name="strain" data-cy="strain" label="Strain" type="select" required>
                <option value="" key="0" />
                {strains
                  ? strains.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/product" replace color="info">
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

export default ProductUpdate;
