import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { TextFormat, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './environmental-alert.reducer';

export const EnvironmentalAlert = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const environmentalAlertList = useAppSelector(state => state.environmentalAlert.entities);
  const loading = useAppSelector(state => state.environmentalAlert.loading);

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
      <h2 id="environmental-alert-heading" data-cy="EnvironmentalAlertHeading">
        Environmental Alerts
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link
            to="/environmental-alert/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Environmental Alert
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {environmentalAlertList && environmentalAlertList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('alertTime')}>
                  Alert Time <FontAwesomeIcon icon={getSortIconByFieldName('alertTime')} />
                </th>
                <th className="hand" onClick={sort('severity')}>
                  Severity <FontAwesomeIcon icon={getSortIconByFieldName('severity')} />
                </th>
                <th className="hand" onClick={sort('parameter')}>
                  Parameter <FontAwesomeIcon icon={getSortIconByFieldName('parameter')} />
                </th>
                <th className="hand" onClick={sort('actualValue')}>
                  Actual Value <FontAwesomeIcon icon={getSortIconByFieldName('actualValue')} />
                </th>
                <th className="hand" onClick={sort('thresholdValue')}>
                  Threshold Value <FontAwesomeIcon icon={getSortIconByFieldName('thresholdValue')} />
                </th>
                <th className="hand" onClick={sort('message')}>
                  Message <FontAwesomeIcon icon={getSortIconByFieldName('message')} />
                </th>
                <th className="hand" onClick={sort('acknowledged')}>
                  Acknowledged <FontAwesomeIcon icon={getSortIconByFieldName('acknowledged')} />
                </th>
                <th className="hand" onClick={sort('acknowledgedBy')}>
                  Acknowledged By <FontAwesomeIcon icon={getSortIconByFieldName('acknowledgedBy')} />
                </th>
                <th className="hand" onClick={sort('acknowledgedAt')}>
                  Acknowledged At <FontAwesomeIcon icon={getSortIconByFieldName('acknowledgedAt')} />
                </th>
                <th className="hand" onClick={sort('resolutionNote')}>
                  Resolution Note <FontAwesomeIcon icon={getSortIconByFieldName('resolutionNote')} />
                </th>
                <th>
                  Room <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Sensor <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Batch <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {environmentalAlertList.map((environmentalAlert, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/environmental-alert/${environmentalAlert.id}`} color="link" size="sm">
                      {environmentalAlert.id}
                    </Button>
                  </td>
                  <td>
                    {environmentalAlert.alertTime ? (
                      <TextFormat type="date" value={environmentalAlert.alertTime} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{environmentalAlert.severity}</td>
                  <td>{environmentalAlert.parameter}</td>
                  <td>{environmentalAlert.actualValue}</td>
                  <td>{environmentalAlert.thresholdValue}</td>
                  <td>{environmentalAlert.message}</td>
                  <td>{environmentalAlert.acknowledged ? 'true' : 'false'}</td>
                  <td>{environmentalAlert.acknowledgedBy}</td>
                  <td>
                    {environmentalAlert.acknowledgedAt ? (
                      <TextFormat type="date" value={environmentalAlert.acknowledgedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{environmentalAlert.resolutionNote}</td>
                  <td>
                    {environmentalAlert.room ? <Link to={`/room/${environmentalAlert.room.id}`}>{environmentalAlert.room.name}</Link> : ''}
                  </td>
                  <td>
                    {environmentalAlert.sensor ? (
                      <Link to={`/sensor/${environmentalAlert.sensor.id}`}>{environmentalAlert.sensor.sensorCode}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {environmentalAlert.batch ? (
                      <Link to={`/batch/${environmentalAlert.batch.id}`}>{environmentalAlert.batch.batchCode}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/environmental-alert/${environmentalAlert.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/environmental-alert/${environmentalAlert.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/environmental-alert/${environmentalAlert.id}/delete`)}
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
          !loading && <div className="alert alert-warning">No Environmental Alerts found</div>
        )}
      </div>
    </div>
  );
};

export default EnvironmentalAlert;
