import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table, Modal, ModalHeader, ModalBody, ModalFooter, Form, FormGroup, Label, Input } from 'reactstrap';
import { TextFormat, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities, generateReport } from './monthly-report.reducer';

export const MonthlyReport = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));
  const [generateModalOpen, setGenerateModalOpen] = useState(false);
  const [reportYear, setReportYear] = useState(new Date().getFullYear());
  const [reportMonth, setReportMonth] = useState(new Date().getMonth() + 1);

  const monthlyReportList = useAppSelector(state => state.monthlyReport.entities);
  const loading = useAppSelector(state => state.monthlyReport.loading);
  const updating = useAppSelector(state => state.monthlyReport.updating);
  const updateSuccess = useAppSelector(state => state.monthlyReport.updateSuccess);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const toggleGenerateModal = () => setGenerateModalOpen(!generateModalOpen);

  const handleGenerateReport = () => {
    dispatch(generateReport({ year: reportYear, month: reportMonth }));
  };

  useEffect(() => {
    if (updateSuccess && generateModalOpen) {
      setGenerateModalOpen(false);
    }
  }, [updateSuccess]);

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="monthly-report-heading" data-cy="MonthlyReportHeading">
        Monthly Reports
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Button className="me-2" color="success" onClick={toggleGenerateModal} id="jh-generate-report">
            <FontAwesomeIcon icon="chart-line" />
            &nbsp; Generate Report
          </Button>
          <Link to="/monthly-report/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Monthly Report
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {monthlyReportList && monthlyReportList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('year')}>
                  Year <FontAwesomeIcon icon={getSortIconByFieldName('year')} />
                </th>
                <th className="hand" onClick={sort('month')}>
                  Month <FontAwesomeIcon icon={getSortIconByFieldName('month')} />
                </th>
                <th className="hand" onClick={sort('generatedAt')}>
                  Generated At <FontAwesomeIcon icon={getSortIconByFieldName('generatedAt')} />
                </th>
                <th className="hand" onClick={sort('totalYieldKg')}>
                  Total Yield Kg <FontAwesomeIcon icon={getSortIconByFieldName('totalYieldKg')} />
                </th>
                <th className="hand" onClick={sort('totalCost')}>
                  Total Cost <FontAwesomeIcon icon={getSortIconByFieldName('totalCost')} />
                </th>
                <th className="hand" onClick={sort('totalRevenue')}>
                  Total Revenue <FontAwesomeIcon icon={getSortIconByFieldName('totalRevenue')} />
                </th>
                <th className="hand" onClick={sort('profitMarginPercent')}>
                  Profit Margin Percent <FontAwesomeIcon icon={getSortIconByFieldName('profitMarginPercent')} />
                </th>
                <th className="hand" onClick={sort('totalContaminationEvents')}>
                  Total Contamination Events <FontAwesomeIcon icon={getSortIconByFieldName('totalContaminationEvents')} />
                </th>
                <th className="hand" onClick={sort('totalMissingFields')}>
                  Total Missing Fields <FontAwesomeIcon icon={getSortIconByFieldName('totalMissingFields')} />
                </th>
                <th className="hand" onClick={sort('summary')}>
                  Summary <FontAwesomeIcon icon={getSortIconByFieldName('summary')} />
                </th>
                <th>
                  Batch <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {monthlyReportList.map((monthlyReport, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/monthly-report/${monthlyReport.id}`} color="link" size="sm">
                      {monthlyReport.id}
                    </Button>
                  </td>
                  <td>{monthlyReport.year}</td>
                  <td>{monthlyReport.month}</td>
                  <td>
                    {monthlyReport.generatedAt ? (
                      <TextFormat type="date" value={monthlyReport.generatedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{monthlyReport.totalYieldKg}</td>
                  <td>{monthlyReport.totalCost}</td>
                  <td>{monthlyReport.totalRevenue}</td>
                  <td>{monthlyReport.profitMarginPercent}</td>
                  <td>{monthlyReport.totalContaminationEvents}</td>
                  <td>{monthlyReport.totalMissingFields}</td>
                  <td>{monthlyReport.summary}</td>
                  <td>{monthlyReport.batch ? <Link to={`/batch/${monthlyReport.batch.id}`}>{monthlyReport.batch.batchCode}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/monthly-report/${monthlyReport.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                        aria-label="View"
                      >
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/monthly-report/${monthlyReport.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                        aria-label="Edit"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/monthly-report/${monthlyReport.id}/delete`)}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                        aria-label="Delete"
                      >
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">No Monthly Reports found</div>
        )}
      </div>

      <Modal isOpen={generateModalOpen} toggle={toggleGenerateModal}>
        <ModalHeader toggle={toggleGenerateModal}>Generate Monthly Report</ModalHeader>
        <ModalBody>
          <Form>
            <FormGroup>
              <Label for="report-year">Year</Label>
              <Input
                type="number"
                id="report-year"
                value={reportYear}
                onChange={e => setReportYear(parseInt(e.target.value, 10))}
                min="2000"
                max="2100"
              />
            </FormGroup>
            <FormGroup>
              <Label for="report-month">Month</Label>
              <Input type="select" id="report-month" value={reportMonth} onChange={e => setReportMonth(parseInt(e.target.value, 10))}>
                <option value="1">January</option>
                <option value="2">February</option>
                <option value="3">March</option>
                <option value="4">April</option>
                <option value="5">May</option>
                <option value="6">June</option>
                <option value="7">July</option>
                <option value="8">August</option>
                <option value="9">September</option>
                <option value="10">October</option>
                <option value="11">November</option>
                <option value="12">December</option>
              </Input>
            </FormGroup>
          </Form>
        </ModalBody>
        <ModalFooter>
          <Button color="secondary" onClick={toggleGenerateModal}>
            Cancel
          </Button>
          <Button color="success" onClick={handleGenerateReport} disabled={updating}>
            {updating ? 'Generating...' : 'Generate Report'}
          </Button>
        </ModalFooter>
      </Modal>
    </div>
  );
};

export default MonthlyReport;
