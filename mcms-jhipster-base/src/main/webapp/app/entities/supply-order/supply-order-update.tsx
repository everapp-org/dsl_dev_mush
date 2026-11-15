import React, { useEffect, useRef } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { LoadingSpinner } from 'app/shared/components/LoadingSpinner';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FormErrorSummary } from 'app/shared/error/form-error-summary';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getSuppliers } from 'app/entities/supplier/supplier.reducer';
import { OrderStatus } from 'app/shared/model/enumerations/order-status.model';
import { createEntity, getEntity, reset, updateEntity } from './supply-order.reducer';

export const SupplyOrderUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const suppliers = useAppSelector(state => state.supplier.entities);
  const supplyOrderEntity = useAppSelector(state => state.supplyOrder.entity);
  const loading = useAppSelector(state => state.supplyOrder.loading);
  const updating = useAppSelector(state => state.supplyOrder.updating);
  const updateSuccess = useAppSelector(state => state.supplyOrder.updateSuccess);

  // Feature #362: Form ref for reset functionality
  const formRef = useRef<any>(null);
  const orderStatusValues = Object.keys(OrderStatus);

  const handleClose = () => {
    navigate('/supply-order', { replace: true });
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getSuppliers({}));
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
    if (values.totalAmount !== undefined && typeof values.totalAmount !== 'number') {
      values.totalAmount = Number(values.totalAmount);
    }

    const entity = {
      ...supplyOrderEntity,
      ...values,
      supplier: suppliers.find(it => it.id.toString() === values.supplier?.toString()),
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
          status: 'ORDERED',
          ...supplyOrderEntity,
          supplier: supplyOrderEntity?.supplier?.id,
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
          <h1 id="mcmsApp.supplyOrder.home.createOrEditLabel" data-cy="SupplyOrderCreateUpdateHeading">
            Create or edit a Supply Order
          </h1>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <LoadingSpinner size="large" message="Loading form..." centered minHeight="200px" />
          ) : (
            <ValidatedForm ref={formRef} defaultValues={defaultValues()} onSubmit={saveEntity}>
              <FormErrorSummary />
              {!isNew ? <ValidatedField name="id" required readOnly id="supply-order-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Order Code"
                id="supply-order-orderCode"
                name="orderCode"
                data-cy="orderCode"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="orderCodeLabel">PO reference number, e.g. &#34;PO-2025-042&#34;</UncontrolledTooltip>
              <ValidatedField
                label="Order Date"
                id="supply-order-orderDate"
                name="orderDate"
                data-cy="orderDate"
                type="date"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="orderDateLabel">Date ordered</UncontrolledTooltip>
              <ValidatedField
                label="Expected Delivery Date"
                id="supply-order-expectedDeliveryDate"
                name="expectedDeliveryDate"
                data-cy="expectedDeliveryDate"
                type="date"
              />
              <UncontrolledTooltip target="expectedDeliveryDateLabel">Expected delivery date</UncontrolledTooltip>
              <ValidatedField
                label="Actual Delivery Date"
                id="supply-order-actualDeliveryDate"
                name="actualDeliveryDate"
                data-cy="actualDeliveryDate"
                type="date"
              />
              <UncontrolledTooltip target="actualDeliveryDateLabel">Actual delivery date</UncontrolledTooltip>
              <ValidatedField label="Status" id="supply-order-status" name="status" data-cy="status" type="select">
                {orderStatusValues.map(orderStatus => (
                  <option value={orderStatus} key={orderStatus}>
                    {orderStatus}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="statusLabel">Current order status</UncontrolledTooltip>
              <ValidatedField label="Total Amount" id="supply-order-totalAmount" name="totalAmount" data-cy="totalAmount" type="text" />
              <UncontrolledTooltip target="totalAmountLabel">Calculated total across all lines</UncontrolledTooltip>
              <ValidatedField
                label="Currency"
                id="supply-order-currency"
                name="currency"
                data-cy="currency"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="currencyLabel">Currency code, e.g. &#34;EUR&#34;, &#34;RSD&#34;</UncontrolledTooltip>
              <ValidatedField
                label="Shipping Address"
                id="supply-order-shippingAddress"
                name="shippingAddress"
                data-cy="shippingAddress"
                type="textarea"
              />
              <UncontrolledTooltip target="shippingAddressLabel">Delivery address (if different from default)</UncontrolledTooltip>
              <ValidatedField label="Note" id="supply-order-note" name="note" data-cy="note" type="textarea" />
              <ValidatedField id="supply-order-supplier" name="supplier" data-cy="supplier" label="Supplier" type="select" required>
                <option value="" key="0">
                  Select supplier...
                </option>
                {suppliers
                  ? suppliers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/supply-order" replace color="info">
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

export default SupplyOrderUpdate;
