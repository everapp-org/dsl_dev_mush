import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './customer.reducer';

export const CustomerUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const customerEntity = useAppSelector(state => state.customer.entity);
  const loading = useAppSelector(state => state.customer.loading);
  const updating = useAppSelector(state => state.customer.updating);
  const updateSuccess = useAppSelector(state => state.customer.updateSuccess);

  const handleClose = () => {
    navigate('/customer');
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

    const entity = {
      ...customerEntity,
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
          ...customerEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mcmsApp.customer.home.createOrEditLabel" data-cy="CustomerCreateUpdateHeading">
            Create or edit a Customer
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="customer-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Name"
                id="customer-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="nameLabel">Business or individual name</UncontrolledTooltip>
              <ValidatedField label="Contact Person" id="customer-contactPerson" name="contactPerson" data-cy="contactPerson" type="text" />
              <ValidatedField label="Email" id="customer-email" name="email" data-cy="email" type="text" />
              <ValidatedField label="Phone" id="customer-phone" name="phone" data-cy="phone" type="text" />
              <ValidatedField label="Address" id="customer-address" name="address" data-cy="address" type="textarea" />
              <ValidatedField
                label="Delivery Preference"
                id="customer-deliveryPreference"
                name="deliveryPreference"
                data-cy="deliveryPreference"
                type="text"
              />
              <UncontrolledTooltip target="deliveryPreferenceLabel">
                e.g. &#34;Next-day cold chain&#34;, &#34;Weekly pickup&#34;
              </UncontrolledTooltip>
              <ValidatedField label="Payment Terms" id="customer-paymentTerms" name="paymentTerms" data-cy="paymentTerms" type="text" />
              <UncontrolledTooltip target="paymentTermsLabel">e.g. &#34;Net 30&#34;, &#34;COD&#34;</UncontrolledTooltip>
              <ValidatedField label="Active" id="customer-active" name="active" data-cy="active" check type="checkbox" />
              <ValidatedField label="Note" id="customer-note" name="note" data-cy="note" type="textarea" />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/customer" replace color="info">
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

export default CustomerUpdate;
