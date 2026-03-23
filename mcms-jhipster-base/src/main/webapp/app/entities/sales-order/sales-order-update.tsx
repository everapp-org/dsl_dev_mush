import React, { useEffect, useRef } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FormErrorSummary } from 'app/shared/error/form-error-summary';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getCustomers } from 'app/entities/customer/customer.reducer';
import { OrderStatus } from 'app/shared/model/enumerations/order-status.model';
import { PaymentStatus } from 'app/shared/model/enumerations/payment-status.model';
import { createEntity, getEntity, reset, updateEntity } from './sales-order.reducer';

export const SalesOrderUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const customers = useAppSelector(state => state.customer.entities);
  const salesOrderEntity = useAppSelector(state => state.salesOrder.entity);
  const loading = useAppSelector(state => state.salesOrder.loading);
  const updating = useAppSelector(state => state.salesOrder.updating);
  const updateSuccess = useAppSelector(state => state.salesOrder.updateSuccess);

  // Feature #362: Form ref for reset functionality
  const formRef = useRef<any>(null);
  const orderStatusValues = Object.keys(OrderStatus);
  const paymentStatusValues = Object.keys(PaymentStatus);

  const handleClose = () => {
    navigate('/sales-order', { replace: true });
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCustomers({}));
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
    if (values.totalWeight !== undefined && typeof values.totalWeight !== 'number') {
      values.totalWeight = Number(values.totalWeight);
    }
    if (values.totalRevenue !== undefined && typeof values.totalRevenue !== 'number') {
      values.totalRevenue = Number(values.totalRevenue);
    }
    if (values.paymentAmount !== undefined && typeof values.paymentAmount !== 'number') {
      values.paymentAmount = Number(values.paymentAmount);
    }

    const entity = {
      ...salesOrderEntity,
      ...values,
      customer: customers.find(it => it.id.toString() === values.customer?.toString()),
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
          paymentStatus: 'UNPAID',
          ...salesOrderEntity,
          customer: salesOrderEntity?.customer?.id,
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
          <h1 id="mcmsApp.salesOrder.home.createOrEditLabel" data-cy="SalesOrderCreateUpdateHeading">
            Create or edit a Sales Order
          </h1>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm ref={formRef} defaultValues={defaultValues()} onSubmit={saveEntity}>
              <FormErrorSummary />
              {!isNew ? <ValidatedField name="id" required readOnly id="sales-order-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Order Code"
                id="sales-order-orderCode"
                name="orderCode"
                data-cy="orderCode"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="orderCodeLabel">Sales order reference, e.g. &#34;SO-2025-108&#34;</UncontrolledTooltip>
              <ValidatedField
                label="Order Date"
                id="sales-order-orderDate"
                name="orderDate"
                data-cy="orderDate"
                type="date"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="orderDateLabel">Date order was placed</UncontrolledTooltip>
              <ValidatedField
                label="Requested Delivery Date"
                id="sales-order-requestedDeliveryDate"
                name="requestedDeliveryDate"
                data-cy="requestedDeliveryDate"
                type="date"
              />
              <UncontrolledTooltip target="requestedDeliveryDateLabel">Customer&#39;s requested delivery date</UncontrolledTooltip>
              <ValidatedField
                label="Actual Delivery Date"
                id="sales-order-actualDeliveryDate"
                name="actualDeliveryDate"
                data-cy="actualDeliveryDate"
                type="date"
              />
              <UncontrolledTooltip target="actualDeliveryDateLabel">Actual delivery date</UncontrolledTooltip>
              <ValidatedField label="Status" id="sales-order-status" name="status" data-cy="status" type="select">
                {orderStatusValues.map(orderStatus => (
                  <option value={orderStatus} key={orderStatus}>
                    {orderStatus}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="statusLabel">Current order status</UncontrolledTooltip>
              <ValidatedField label="Total Weight" id="sales-order-totalWeight" name="totalWeight" data-cy="totalWeight" type="text" />
              <UncontrolledTooltip target="totalWeightLabel">Calculated total weight across all lines (kg)</UncontrolledTooltip>
              <ValidatedField label="Total Revenue" id="sales-order-totalRevenue" name="totalRevenue" data-cy="totalRevenue" type="text" />
              <UncontrolledTooltip target="totalRevenueLabel">Calculated total revenue across all lines</UncontrolledTooltip>
              <ValidatedField
                label="Currency"
                id="sales-order-currency"
                name="currency"
                data-cy="currency"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="currencyLabel">Currency code</UncontrolledTooltip>
              <ValidatedField
                label="Invoice Number"
                id="sales-order-invoiceNumber"
                name="invoiceNumber"
                data-cy="invoiceNumber"
                type="text"
              />
              <UncontrolledTooltip target="invoiceNumberLabel">Linked invoice reference</UncontrolledTooltip>
              <ValidatedField
                label="Payment Status"
                id="sales-order-paymentStatus"
                name="paymentStatus"
                data-cy="paymentStatus"
                type="select"
              >
                {paymentStatusValues.map(paymentStatus => (
                  <option value={paymentStatus} key={paymentStatus}>
                    {paymentStatus}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="paymentStatusLabel">Payment tracking (replaces simple isPaid boolean)</UncontrolledTooltip>
              <ValidatedField
                label="Payment Due Date"
                id="sales-order-paymentDueDate"
                name="paymentDueDate"
                data-cy="paymentDueDate"
                type="date"
              />
              <UncontrolledTooltip target="paymentDueDateLabel">When payment is due</UncontrolledTooltip>
              <ValidatedField
                label="Payment Received Date"
                id="sales-order-paymentReceivedDate"
                name="paymentReceivedDate"
                data-cy="paymentReceivedDate"
                type="date"
              />
              <UncontrolledTooltip target="paymentReceivedDateLabel">When payment was received</UncontrolledTooltip>
              <ValidatedField
                label="Payment Amount"
                id="sales-order-paymentAmount"
                name="paymentAmount"
                data-cy="paymentAmount"
                type="text"
                validate={{
                  min: { value: 0, message: 'Payment amount must be positive' },
                  validate: {
                    notExceedTotal(value, formValues) {
                      if (!value) return true; // Allow empty
                      const totalRevenue = formValues.totalRevenue || salesOrderEntity?.totalRevenue;
                      if (totalRevenue && Number(value) > Number(totalRevenue)) {
                        return 'Payment amount cannot exceed order total';
                      }
                      return true;
                    },
                  },
                }}
              />
              <UncontrolledTooltip target="paymentAmountLabel">Amount paid (must not exceed total revenue)</UncontrolledTooltip>
              <ValidatedField
                label="Shipping Address"
                id="sales-order-shippingAddress"
                name="shippingAddress"
                data-cy="shippingAddress"
                type="textarea"
              />
              <UncontrolledTooltip target="shippingAddressLabel">Delivery address</UncontrolledTooltip>
              <ValidatedField label="Note" id="sales-order-note" name="note" data-cy="note" type="textarea" />
              <ValidatedField id="sales-order-customer" name="customer" data-cy="customer" label="Customer" type="select" required>
                <option value="" key="0">
                  Select customer...
                </option>
                {customers
                  ? customers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/sales-order" replace color="info">
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

export default SalesOrderUpdate;
