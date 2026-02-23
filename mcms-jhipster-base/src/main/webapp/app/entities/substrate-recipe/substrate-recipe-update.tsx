import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { SubstrateBase } from 'app/shared/model/enumerations/substrate-base.model';
import { createEntity, getEntity, reset, updateEntity } from './substrate-recipe.reducer';

export const SubstrateRecipeUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const substrateRecipeEntity = useAppSelector(state => state.substrateRecipe.entity);
  const loading = useAppSelector(state => state.substrateRecipe.loading);
  const updating = useAppSelector(state => state.substrateRecipe.updating);
  const updateSuccess = useAppSelector(state => state.substrateRecipe.updateSuccess);
  const substrateBaseValues = Object.keys(SubstrateBase);

  const handleClose = () => {
    navigate('/substrate-recipe');
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
    if (values.moistureTargetPercent !== undefined && typeof values.moistureTargetPercent !== 'number') {
      values.moistureTargetPercent = Number(values.moistureTargetPercent);
    }
    if (values.phTarget !== undefined && typeof values.phTarget !== 'number') {
      values.phTarget = Number(values.phTarget);
    }

    const entity = {
      ...substrateRecipeEntity,
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
          baseType: 'WHEAT_STRAW',
          ...substrateRecipeEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mcmsApp.substrateRecipe.home.createOrEditLabel" data-cy="SubstrateRecipeCreateUpdateHeading">
            Create or edit a Substrate Recipe
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
                <ValidatedField name="id" required readOnly id="substrate-recipe-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Name"
                id="substrate-recipe-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="nameLabel">e.g. &#34;Standard Wheat Straw v3&#34;</UncontrolledTooltip>
              <ValidatedField
                label="Version"
                id="substrate-recipe-version"
                name="version"
                data-cy="version"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="versionLabel">Recipe version identifier</UncontrolledTooltip>
              <ValidatedField label="Base Type" id="substrate-recipe-baseType" name="baseType" data-cy="baseType" type="select">
                {substrateBaseValues.map(substrateBase => (
                  <option value={substrateBase} key={substrateBase}>
                    {substrateBase}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="baseTypeLabel">Primary substrate material</UncontrolledTooltip>
              <ValidatedField
                label="Composition Detail"
                id="substrate-recipe-compositionDetail"
                name="compositionDetail"
                data-cy="compositionDetail"
                type="textarea"
              />
              <UncontrolledTooltip target="compositionDetailLabel">Full ingredient list &amp; ratios</UncontrolledTooltip>
              <ValidatedField
                label="Sterilization Method"
                id="substrate-recipe-sterilizationMethod"
                name="sterilizationMethod"
                data-cy="sterilizationMethod"
                type="text"
              />
              <UncontrolledTooltip target="sterilizationMethodLabel">
                e.g. &#34;Autoclave 121°C/2h&#34;, &#34;Pasteurization 80°C/8h&#34;
              </UncontrolledTooltip>
              <ValidatedField
                label="Moisture Target Percent"
                id="substrate-recipe-moistureTargetPercent"
                name="moistureTargetPercent"
                data-cy="moistureTargetPercent"
                type="text"
              />
              <UncontrolledTooltip target="moistureTargetPercentLabel">Target moisture content %</UncontrolledTooltip>
              <ValidatedField label="Ph Target" id="substrate-recipe-phTarget" name="phTarget" data-cy="phTarget" type="text" />
              <UncontrolledTooltip target="phTargetLabel">Target pH level</UncontrolledTooltip>
              <ValidatedField
                label="Supplement Notes"
                id="substrate-recipe-supplementNotes"
                name="supplementNotes"
                data-cy="supplementNotes"
                type="textarea"
              />
              <UncontrolledTooltip target="supplementNotesLabel">Supplements: gypsum, bran, lime, etc.</UncontrolledTooltip>
              <ValidatedField label="Active" id="substrate-recipe-active" name="active" data-cy="active" check type="checkbox" />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/substrate-recipe" replace color="info">
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

export default SubstrateRecipeUpdate;
