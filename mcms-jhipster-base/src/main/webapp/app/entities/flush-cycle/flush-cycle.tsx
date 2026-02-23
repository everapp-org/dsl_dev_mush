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

import { getEntities } from './flush-cycle.reducer';

export const FlushCycle = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const flushCycleList = useAppSelector(state => state.flushCycle.entities);
  const loading = useAppSelector(state => state.flushCycle.loading);

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
      <h2 id="flush-cycle-heading" data-cy="FlushCycleHeading">
        Flush Cycles
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link to="/flush-cycle/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Flush Cycle
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {flushCycleList && flushCycleList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('flushNumber')}>
                  Flush Number <FontAwesomeIcon icon={getSortIconByFieldName('flushNumber')} />
                </th>
                <th className="hand" onClick={sort('harvestStartDate')}>
                  Harvest Start Date <FontAwesomeIcon icon={getSortIconByFieldName('harvestStartDate')} />
                </th>
                <th className="hand" onClick={sort('harvestEndDate')}>
                  Harvest End Date <FontAwesomeIcon icon={getSortIconByFieldName('harvestEndDate')} />
                </th>
                <th className="hand" onClick={sort('yieldKg')}>
                  Yield Kg <FontAwesomeIcon icon={getSortIconByFieldName('yieldKg')} />
                </th>
                <th className="hand" onClick={sort('yieldBagsHarvested')}>
                  Yield Bags Harvested <FontAwesomeIcon icon={getSortIconByFieldName('yieldBagsHarvested')} />
                </th>
                <th className="hand" onClick={sort('avgFruitBodyWeightG')}>
                  Avg Fruit Body Weight G <FontAwesomeIcon icon={getSortIconByFieldName('avgFruitBodyWeightG')} />
                </th>
                <th className="hand" onClick={sort('rehydrationDone')}>
                  Rehydration Done <FontAwesomeIcon icon={getSortIconByFieldName('rehydrationDone')} />
                </th>
                <th className="hand" onClick={sort('rehydrationDurationHours')}>
                  Rehydration Duration Hours <FontAwesomeIcon icon={getSortIconByFieldName('rehydrationDurationHours')} />
                </th>
                <th className="hand" onClick={sort('note')}>
                  Note <FontAwesomeIcon icon={getSortIconByFieldName('note')} />
                </th>
                <th>
                  Batch <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {flushCycleList.map((flushCycle, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/flush-cycle/${flushCycle.id}`} color="link" size="sm">
                      {flushCycle.id}
                    </Button>
                  </td>
                  <td>{flushCycle.flushNumber}</td>
                  <td>
                    {flushCycle.harvestStartDate ? (
                      <TextFormat type="date" value={flushCycle.harvestStartDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {flushCycle.harvestEndDate ? (
                      <TextFormat type="date" value={flushCycle.harvestEndDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{flushCycle.yieldKg}</td>
                  <td>{flushCycle.yieldBagsHarvested}</td>
                  <td>{flushCycle.avgFruitBodyWeightG}</td>
                  <td>{flushCycle.rehydrationDone ? 'true' : 'false'}</td>
                  <td>{flushCycle.rehydrationDurationHours}</td>
                  <td>{flushCycle.note}</td>
                  <td>{flushCycle.batch ? <Link to={`/batch/${flushCycle.batch.id}`}>{flushCycle.batch.batchCode}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/flush-cycle/${flushCycle.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button tag={Link} to={`/flush-cycle/${flushCycle.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/flush-cycle/${flushCycle.id}/delete`)}
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
          !loading && <div className="alert alert-warning">No Flush Cycles found</div>
        )}
      </div>
    </div>
  );
};

export default FlushCycle;
