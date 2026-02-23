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

import { getEntities } from './harvest-record.reducer';

export const HarvestRecord = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const harvestRecordList = useAppSelector(state => state.harvestRecord.entities);
  const loading = useAppSelector(state => state.harvestRecord.loading);

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
      <h2 id="harvest-record-heading" data-cy="HarvestRecordHeading">
        Harvest Records
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link to="/harvest-record/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Harvest Record
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {harvestRecordList && harvestRecordList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('harvestDate')}>
                  Harvest Date <FontAwesomeIcon icon={getSortIconByFieldName('harvestDate')} />
                </th>
                <th className="hand" onClick={sort('weightKg')}>
                  Weight Kg <FontAwesomeIcon icon={getSortIconByFieldName('weightKg')} />
                </th>
                <th className="hand" onClick={sort('grade')}>
                  Grade <FontAwesomeIcon icon={getSortIconByFieldName('grade')} />
                </th>
                <th className="hand" onClick={sort('pickerName')}>
                  Picker Name <FontAwesomeIcon icon={getSortIconByFieldName('pickerName')} />
                </th>
                <th className="hand" onClick={sort('note')}>
                  Note <FontAwesomeIcon icon={getSortIconByFieldName('note')} />
                </th>
                <th>
                  Flush Cycle <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {harvestRecordList.map((harvestRecord, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/harvest-record/${harvestRecord.id}`} color="link" size="sm">
                      {harvestRecord.id}
                    </Button>
                  </td>
                  <td>
                    {harvestRecord.harvestDate ? (
                      <TextFormat type="date" value={harvestRecord.harvestDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{harvestRecord.weightKg}</td>
                  <td>{harvestRecord.grade}</td>
                  <td>{harvestRecord.pickerName}</td>
                  <td>{harvestRecord.note}</td>
                  <td>
                    {harvestRecord.flushCycle ? (
                      <Link to={`/flush-cycle/${harvestRecord.flushCycle.id}`}>{harvestRecord.flushCycle.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/harvest-record/${harvestRecord.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/harvest-record/${harvestRecord.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/harvest-record/${harvestRecord.id}/delete`)}
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
          !loading && <div className="alert alert-warning">No Harvest Records found</div>
        )}
      </div>
    </div>
  );
};

export default HarvestRecord;
