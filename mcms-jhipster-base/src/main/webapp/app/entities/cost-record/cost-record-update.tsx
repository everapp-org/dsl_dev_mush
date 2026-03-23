import React, { useEffect, useRef } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getBatches } from 'app/entities/batch/batch.reducer';
import { CostCategory } from 'app/shared/model/enumerations/cost-category.model';
import { createEntity, getEntity, reset, updateEntity } from './cost-record.reducer';

export const CostRecordUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const batches = useAppSelector(state => state.batch.entities);
  const costRecordEntity = useAppSelector(state => state.costRecord.entity);
  const loading = useAppSelector(state => state.costRecord.loading);
  const updating = useAppSelector(state => state.costRecord.updating);
  const updateSuccess = useAppSelector(state => state.costRecord.updateSuccess);

  // Feature #362: Form ref for reset functionality
  const formRef = useRef<any>(null);
  const costCategoryValues = Object.keys(CostCategory);

  const handleClose = () => {
    navigate('/cost-record', { replace: true });
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getBatches({}));
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
    if (values.amount !== undefined && typeof values.amount !== 'number') {
      values.amount = Number(values.amount);
    }

    const entity = {
      ...costRecordEntity,
      ...values,
      batch: batches.find(it => it.id.toString() === values.batch?.toString()),
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
          category: 'RAW_MATERIALS',
          ...costRecordEntity,
          batch: costRecordEntity?.batch?.id,
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
          <h1 id="mcmsApp.costRecord.home.createOrEditLabel" data-cy="CostRecordCreateUpdateHeading">
            Create or edit a Cost Record
          </h1>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm ref={formRef} defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="cost-record-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Record Date"
                id="cost-record-recordDate"
                name="recordDate"
                data-cy="recordDate"
                type="date"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="recordDateLabel">Date of expense</UncontrolledTooltip>
              <ValidatedField label="Category" id="cost-record-category" name="category" data-cy="category" type="select">
                {costCategoryValues.map(costCategory => (
                  <option value={costCategory} key={costCategory}>
                    {costCategory}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="categoryLabel">What type of cost</UncontrolledTooltip>
              <ValidatedField
                label="Description"
                id="cost-record-description"
                name="description"
                data-cy="description"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="descriptionLabel">Brief description</UncontrolledTooltip>
              <ValidatedField
                label="Amount"
                id="cost-record-amount"
                name="amount"
                data-cy="amount"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="amountLabel">Cost amount in local currency</UncontrolledTooltip>
              <ValidatedField
                label="Currency"
                id="cost-record-currency"
                name="currency"
                data-cy="currency"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="currencyLabel">Currency code, e.g. &#34;RSD&#34;, &#34;EUR&#34;</UncontrolledTooltip>
              <ValidatedField label="Note" id="cost-record-note" name="note" data-cy="note" type="textarea" />
              <ValidatedField id="cost-record-batch" name="batch" data-cy="batch" label="Batch" type="select" required>
                <option value="" key="0">
                Select batch...
              </option>
                {batches
                  ? batches.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.batchCode}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/cost-record" replace color="info">
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

export default CostRecordUpdate;
