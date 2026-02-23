import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { TextFormat, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './mandatory-field-check.reducer';

export const MandatoryFieldCheck = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const mandatoryFieldCheckList = useAppSelector(state => state.mandatoryFieldCheck.entities);
  const loading = useAppSelector(state => state.mandatoryFieldCheck.loading);

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
      <h2 id="mandatory-field-check-heading" data-cy="MandatoryFieldCheckHeading">
        Mandatory Field Checks
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link
            to="/mandatory-field-check/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Mandatory Field Check
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {mandatoryFieldCheckList && mandatoryFieldCheckList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('fieldName')}>
                  Field Name <FontAwesomeIcon icon={getSortIconByFieldName('fieldName')} />
                </th>
                <th className="hand" onClick={sort('isFilled')}>
                  Is Filled <FontAwesomeIcon icon={getSortIconByFieldName('isFilled')} />
                </th>
                <th className="hand" onClick={sort('checkDate')}>
                  Check Date <FontAwesomeIcon icon={getSortIconByFieldName('checkDate')} />
                </th>
                <th>
                  Phase Execution <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {mandatoryFieldCheckList.map((mandatoryFieldCheck, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/mandatory-field-check/${mandatoryFieldCheck.id}`} color="link" size="sm">
                      {mandatoryFieldCheck.id}
                    </Button>
                  </td>
                  <td>{mandatoryFieldCheck.fieldName}</td>
                  <td>{mandatoryFieldCheck.isFilled ? 'true' : 'false'}</td>
                  <td>
                    {mandatoryFieldCheck.checkDate ? (
                      <TextFormat type="date" value={mandatoryFieldCheck.checkDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {mandatoryFieldCheck.phaseExecution ? (
                      <Link to={`/phase-execution/${mandatoryFieldCheck.phaseExecution.id}`}>{mandatoryFieldCheck.phaseExecution.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/mandatory-field-check/${mandatoryFieldCheck.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/mandatory-field-check/${mandatoryFieldCheck.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/mandatory-field-check/${mandatoryFieldCheck.id}/delete`)}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
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
          !loading && <div className="alert alert-warning">No Mandatory Field Checks found</div>
        )}
      </div>
    </div>
  );
};

export default MandatoryFieldCheck;
