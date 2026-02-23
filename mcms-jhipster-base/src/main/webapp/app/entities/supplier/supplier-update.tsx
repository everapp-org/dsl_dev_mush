import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { SupplierType } from 'app/shared/model/enumerations/supplier-type.model';
import { createEntity, getEntity, reset, updateEntity } from './supplier.reducer';

export const SupplierUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const supplierEntity = useAppSelector(state => state.supplier.entity);
  const loading = useAppSelector(state => state.supplier.loading);
  const updating = useAppSelector(state => state.supplier.updating);
  const updateSuccess = useAppSelector(state => state.supplier.updateSuccess);
  const supplierTypeValues = Object.keys(SupplierType);

  const handleClose = () => {
    navigate('/supplier');
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
    if (values.rating !== undefined && typeof values.rating !== 'number') {
      values.rating = Number(values.rating);
    }

    const entity = {
      ...supplierEntity,
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
          type: 'SPAWN',
          ...supplierEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mcmsApp.supplier.home.createOrEditLabel" data-cy="SupplierCreateUpdateHeading">
            Create or edit a Supplier
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="supplier-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Name"
                id="supplier-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="nameLabel">Company or individual name</UncontrolledTooltip>
              <ValidatedField label="Type" id="supplier-type" name="type" data-cy="type" type="select">
                {supplierTypeValues.map(supplierType => (
                  <option value={supplierType} key={supplierType}>
                    {supplierType}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="typeLabel">What they supply</UncontrolledTooltip>
              <ValidatedField label="Contact Person" id="supplier-contactPerson" name="contactPerson" data-cy="contactPerson" type="text" />
              <UncontrolledTooltip target="contactPersonLabel">Primary contact name</UncontrolledTooltip>
              <ValidatedField label="Email" id="supplier-email" name="email" data-cy="email" type="text" />
              <UncontrolledTooltip target="emailLabel">Email</UncontrolledTooltip>
              <ValidatedField label="Phone" id="supplier-phone" name="phone" data-cy="phone" type="text" />
              <UncontrolledTooltip target="phoneLabel">Phone number</UncontrolledTooltip>
              <ValidatedField label="Address" id="supplier-address" name="address" data-cy="address" type="textarea" />
              <UncontrolledTooltip target="addressLabel">Full address</UncontrolledTooltip>
              <ValidatedField
                label="Rating"
                id="supplier-rating"
                name="rating"
                data-cy="rating"
                type="text"
                validate={{
                  min: { value: 1, message: 'This field should be at least 1.' },
                  max: { value: 5, message: 'This field cannot be more than 5.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="ratingLabel">Internal quality rating 1-5</UncontrolledTooltip>
              <ValidatedField label="Active" id="supplier-active" name="active" data-cy="active" check type="checkbox" />
              <ValidatedField label="Note" id="supplier-note" name="note" data-cy="note" type="textarea" />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/supplier" replace color="info">
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

export default SupplierUpdate;
