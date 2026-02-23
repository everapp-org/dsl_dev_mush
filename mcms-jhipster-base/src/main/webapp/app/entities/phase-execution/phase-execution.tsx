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

import { getEntities } from './phase-execution.reducer';

export const PhaseExecution = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const phaseExecutionList = useAppSelector(state => state.phaseExecution.entities);
  const loading = useAppSelector(state => state.phaseExecution.loading);

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
      <h2 id="phase-execution-heading" data-cy="PhaseExecutionHeading">
        Phase Executions
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link to="/phase-execution/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Phase Execution
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {phaseExecutionList && phaseExecutionList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('phase')}>
                  Phase <FontAwesomeIcon icon={getSortIconByFieldName('phase')} />
                </th>
                <th className="hand" onClick={sort('sequenceOrder')}>
                  Sequence Order <FontAwesomeIcon icon={getSortIconByFieldName('sequenceOrder')} />
                </th>
                <th className="hand" onClick={sort('startDate')}>
                  Start Date <FontAwesomeIcon icon={getSortIconByFieldName('startDate')} />
                </th>
                <th className="hand" onClick={sort('endDate')}>
                  End Date <FontAwesomeIcon icon={getSortIconByFieldName('endDate')} />
                </th>
                <th className="hand" onClick={sort('plannedDurationDays')}>
                  Planned Duration Days <FontAwesomeIcon icon={getSortIconByFieldName('plannedDurationDays')} />
                </th>
                <th className="hand" onClick={sort('actualDurationDays')}>
                  Actual Duration Days <FontAwesomeIcon icon={getSortIconByFieldName('actualDurationDays')} />
                </th>
                <th className="hand" onClick={sort('responsiblePerson')}>
                  Responsible Person <FontAwesomeIcon icon={getSortIconByFieldName('responsiblePerson')} />
                </th>
                <th className="hand" onClick={sort('note')}>
                  Note <FontAwesomeIcon icon={getSortIconByFieldName('note')} />
                </th>
                <th>
                  Batch <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Room <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {phaseExecutionList.map((phaseExecution, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/phase-execution/${phaseExecution.id}`} color="link" size="sm">
                      {phaseExecution.id}
                    </Button>
                  </td>
                  <td>{phaseExecution.phase}</td>
                  <td>{phaseExecution.sequenceOrder}</td>
                  <td>
                    {phaseExecution.startDate ? (
                      <TextFormat type="date" value={phaseExecution.startDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {phaseExecution.endDate ? (
                      <TextFormat type="date" value={phaseExecution.endDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{phaseExecution.plannedDurationDays}</td>
                  <td>{phaseExecution.actualDurationDays}</td>
                  <td>{phaseExecution.responsiblePerson}</td>
                  <td>{phaseExecution.note}</td>
                  <td>
                    {phaseExecution.batch ? <Link to={`/batch/${phaseExecution.batch.id}`}>{phaseExecution.batch.batchCode}</Link> : ''}
                  </td>
                  <td>{phaseExecution.room ? <Link to={`/room/${phaseExecution.room.id}`}>{phaseExecution.room.name}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/phase-execution/${phaseExecution.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/phase-execution/${phaseExecution.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/phase-execution/${phaseExecution.id}/delete`)}
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
          !loading && <div className="alert alert-warning">No Phase Executions found</div>
        )}
      </div>
    </div>
  );
};

export default PhaseExecution;
