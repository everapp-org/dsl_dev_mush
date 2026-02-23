import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getBatches } from 'app/entities/batch/batch.reducer';
import { createEntity, getEntity, reset, updateEntity } from './monthly-report.reducer';

export const MonthlyReportUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const batches = useAppSelector(state => state.batch.entities);
  const monthlyReportEntity = useAppSelector(state => state.monthlyReport.entity);
  const loading = useAppSelector(state => state.monthlyReport.loading);
  const updating = useAppSelector(state => state.monthlyReport.updating);
  const updateSuccess = useAppSelector(state => state.monthlyReport.updateSuccess);

  const handleClose = () => {
    navigate('/monthly-report');
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
    if (values.year !== undefined && typeof values.year !== 'number') {
      values.year = Number(values.year);
    }
    if (values.month !== undefined && typeof values.month !== 'number') {
      values.month = Number(values.month);
    }
    values.generatedAt = convertDateTimeToServer(values.generatedAt);
    if (values.totalYieldKg !== undefined && typeof values.totalYieldKg !== 'number') {
      values.totalYieldKg = Number(values.totalYieldKg);
    }
    if (values.totalCost !== undefined && typeof values.totalCost !== 'number') {
      values.totalCost = Number(values.totalCost);
    }
    if (values.totalRevenue !== undefined && typeof values.totalRevenue !== 'number') {
      values.totalRevenue = Number(values.totalRevenue);
    }
    if (values.profitMarginPercent !== undefined && typeof values.profitMarginPercent !== 'number') {
      values.profitMarginPercent = Number(values.profitMarginPercent);
    }
    if (values.totalContaminationEvents !== undefined && typeof values.totalContaminationEvents !== 'number') {
      values.totalContaminationEvents = Number(values.totalContaminationEvents);
    }
    if (values.totalMissingFields !== undefined && typeof values.totalMissingFields !== 'number') {
      values.totalMissingFields = Number(values.totalMissingFields);
    }

    const entity = {
      ...monthlyReportEntity,
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
      ? {
          generatedAt: displayDefaultDateTime(),
        }
      : {
          ...monthlyReportEntity,
          generatedAt: convertDateTimeFromServer(monthlyReportEntity.generatedAt),
          batch: monthlyReportEntity?.batch?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mcmsApp.monthlyReport.home.createOrEditLabel" data-cy="MonthlyReportCreateUpdateHeading">
            Create or edit a Monthly Report
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
                <ValidatedField name="id" required readOnly id="monthly-report-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Year"
                id="monthly-report-year"
                name="year"
                data-cy="year"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="yearLabel">Year</UncontrolledTooltip>
              <ValidatedField
                label="Month"
                id="monthly-report-month"
                name="month"
                data-cy="month"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  min: { value: 1, message: 'This field should be at least 1.' },
                  max: { value: 12, message: 'This field cannot be more than 12.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="monthLabel">Month (1-12)</UncontrolledTooltip>
              <ValidatedField
                label="Generated At"
                id="monthly-report-generatedAt"
                name="generatedAt"
                data-cy="generatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="generatedAtLabel">Report generation timestamp</UncontrolledTooltip>
              <ValidatedField
                label="Total Yield Kg"
                id="monthly-report-totalYieldKg"
                name="totalYieldKg"
                data-cy="totalYieldKg"
                type="text"
              />
              <UncontrolledTooltip target="totalYieldKgLabel">Total yield in reporting period</UncontrolledTooltip>
              <ValidatedField label="Total Cost" id="monthly-report-totalCost" name="totalCost" data-cy="totalCost" type="text" />
              <UncontrolledTooltip target="totalCostLabel">Total costs in reporting period</UncontrolledTooltip>
              <ValidatedField
                label="Total Revenue"
                id="monthly-report-totalRevenue"
                name="totalRevenue"
                data-cy="totalRevenue"
                type="text"
              />
              <UncontrolledTooltip target="totalRevenueLabel">Total revenue in reporting period</UncontrolledTooltip>
              <ValidatedField
                label="Profit Margin Percent"
                id="monthly-report-profitMarginPercent"
                name="profitMarginPercent"
                data-cy="profitMarginPercent"
                type="text"
              />
              <UncontrolledTooltip target="profitMarginPercentLabel">Calculated profit margin</UncontrolledTooltip>
              <ValidatedField
                label="Total Contamination Events"
                id="monthly-report-totalContaminationEvents"
                name="totalContaminationEvents"
                data-cy="totalContaminationEvents"
                type="text"
              />
              <UncontrolledTooltip target="totalContaminationEventsLabel">Count of contamination events</UncontrolledTooltip>
              <ValidatedField
                label="Total Missing Fields"
                id="monthly-report-totalMissingFields"
                name="totalMissingFields"
                data-cy="totalMissingFields"
                type="text"
              />
              <UncontrolledTooltip target="totalMissingFieldsLabel">Count of incomplete mandatory fields</UncontrolledTooltip>
              <ValidatedField label="Summary" id="monthly-report-summary" name="summary" data-cy="summary" type="textarea" />
              <UncontrolledTooltip target="summaryLabel">Executive summary text</UncontrolledTooltip>
              <ValidatedField id="monthly-report-batch" name="batch" data-cy="batch" label="Batch" type="select">
                <option value="" key="0" />
                {batches
                  ? batches.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.batchCode}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/monthly-report" replace color="info">
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

export default MonthlyReportUpdate;
