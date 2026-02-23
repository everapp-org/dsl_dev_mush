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

import { getEntities } from './batch.reducer';

export const Batch = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const batchList = useAppSelector(state => state.batch.entities);
  const loading = useAppSelector(state => state.batch.loading);

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
      <h2 id="batch-heading" data-cy="BatchHeading">
        Batches
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link to="/batch/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Batch
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {batchList && batchList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('batchCode')}>
                  Batch Code <FontAwesomeIcon icon={getSortIconByFieldName('batchCode')} />
                </th>
                <th className="hand" onClick={sort('startDate')}>
                  Start Date <FontAwesomeIcon icon={getSortIconByFieldName('startDate')} />
                </th>
                <th className="hand" onClick={sort('endDate')}>
                  End Date <FontAwesomeIcon icon={getSortIconByFieldName('endDate')} />
                </th>
                <th className="hand" onClick={sort('currentPhase')}>
                  Current Phase <FontAwesomeIcon icon={getSortIconByFieldName('currentPhase')} />
                </th>
                <th className="hand" onClick={sort('numberOfBags')}>
                  Number Of Bags <FontAwesomeIcon icon={getSortIconByFieldName('numberOfBags')} />
                </th>
                <th className="hand" onClick={sort('substrateWeightKg')}>
                  Substrate Weight Kg <FontAwesomeIcon icon={getSortIconByFieldName('substrateWeightKg')} />
                </th>
                <th className="hand" onClick={sort('spawnWeightKg')}>
                  Spawn Weight Kg <FontAwesomeIcon icon={getSortIconByFieldName('spawnWeightKg')} />
                </th>
                <th className="hand" onClick={sort('targetYieldKg')}>
                  Target Yield Kg <FontAwesomeIcon icon={getSortIconByFieldName('targetYieldKg')} />
                </th>
                <th className="hand" onClick={sort('actualTotalYieldKg')}>
                  Actual Total Yield Kg <FontAwesomeIcon icon={getSortIconByFieldName('actualTotalYieldKg')} />
                </th>
                <th className="hand" onClick={sort('biologicalEfficiencyPercent')}>
                  Biological Efficiency Percent <FontAwesomeIcon icon={getSortIconByFieldName('biologicalEfficiencyPercent')} />
                </th>
                <th className="hand" onClick={sort('isContaminated')}>
                  Is Contaminated <FontAwesomeIcon icon={getSortIconByFieldName('isContaminated')} />
                </th>
                <th className="hand" onClick={sort('isActive')}>
                  Is Active <FontAwesomeIcon icon={getSortIconByFieldName('isActive')} />
                </th>
                <th className="hand" onClick={sort('completionNote')}>
                  Completion Note <FontAwesomeIcon icon={getSortIconByFieldName('completionNote')} />
                </th>
                <th className="hand" onClick={sort('note')}>
                  Note <FontAwesomeIcon icon={getSortIconByFieldName('note')} />
                </th>
                <th>
                  Strain <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Recipe <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {batchList.map((batch, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/batch/${batch.id}`} color="link" size="sm">
                      {batch.id}
                    </Button>
                  </td>
                  <td>{batch.batchCode}</td>
                  <td>{batch.startDate ? <TextFormat type="date" value={batch.startDate} format={APP_LOCAL_DATE_FORMAT} /> : null}</td>
                  <td>{batch.endDate ? <TextFormat type="date" value={batch.endDate} format={APP_LOCAL_DATE_FORMAT} /> : null}</td>
                  <td>{batch.currentPhase}</td>
                  <td>{batch.numberOfBags}</td>
                  <td>{batch.substrateWeightKg}</td>
                  <td>{batch.spawnWeightKg}</td>
                  <td>{batch.targetYieldKg}</td>
                  <td>{batch.actualTotalYieldKg}</td>
                  <td>{batch.biologicalEfficiencyPercent}</td>
                  <td>{batch.isContaminated ? 'true' : 'false'}</td>
                  <td>{batch.isActive ? 'true' : 'false'}</td>
                  <td>{batch.completionNote}</td>
                  <td>{batch.note}</td>
                  <td>{batch.strain ? <Link to={`/strain/${batch.strain.id}`}>{batch.strain.name}</Link> : ''}</td>
                  <td>{batch.recipe ? <Link to={`/substrate-recipe/${batch.recipe.id}`}>{batch.recipe.name}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/batch/${batch.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button tag={Link} to={`/batch/${batch.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/batch/${batch.id}/delete`)}
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
          !loading && <div className="alert alert-warning">No Batches found</div>
        )}
      </div>
    </div>
  );
};

export default Batch;
