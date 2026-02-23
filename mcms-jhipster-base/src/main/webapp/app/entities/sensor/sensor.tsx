import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { TextFormat, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_LOCAL_DATE_FORMAT, AUTHORITIES } from 'app/config/constants';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';

import { getEntities } from './sensor.reducer';

export const Sensor = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const sensorList = useAppSelector(state => state.sensor.entities);
  const loading = useAppSelector(state => state.sensor.loading);
  const canManageSensors = useAppSelector(state =>
    hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN, AUTHORITIES.MANAGER]),
  );

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
      <h2 id="sensor-heading" data-cy="SensorHeading">
        Sensors
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          {canManageSensors && (
            <Link to="/sensor/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
              <FontAwesomeIcon icon="plus" />
              &nbsp; Create a new Sensor
            </Link>
          )}
        </div>
      </h2>
      <div className="table-responsive">
        {sensorList && sensorList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('sensorCode')}>
                  Sensor Code <FontAwesomeIcon icon={getSortIconByFieldName('sensorCode')} />
                </th>
                <th className="hand" onClick={sort('sensorType')}>
                  Sensor Type <FontAwesomeIcon icon={getSortIconByFieldName('sensorType')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  Status <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('installedDate')}>
                  Installed Date <FontAwesomeIcon icon={getSortIconByFieldName('installedDate')} />
                </th>
                <th className="hand" onClick={sort('lastCalibrationDate')}>
                  Last Calibration Date <FontAwesomeIcon icon={getSortIconByFieldName('lastCalibrationDate')} />
                </th>
                <th className="hand" onClick={sort('manufacturer')}>
                  Manufacturer <FontAwesomeIcon icon={getSortIconByFieldName('manufacturer')} />
                </th>
                <th className="hand" onClick={sort('model')}>
                  Model <FontAwesomeIcon icon={getSortIconByFieldName('model')} />
                </th>
                <th className="hand" onClick={sort('note')}>
                  Note <FontAwesomeIcon icon={getSortIconByFieldName('note')} />
                </th>
                <th>
                  Room <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {sensorList.map((sensor, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/sensor/${sensor.id}`} color="link" size="sm">
                      {sensor.id}
                    </Button>
                  </td>
                  <td>{sensor.sensorCode}</td>
                  <td>{sensor.sensorType}</td>
                  <td>{sensor.status}</td>
                  <td>
                    {sensor.installedDate ? <TextFormat type="date" value={sensor.installedDate} format={APP_LOCAL_DATE_FORMAT} /> : null}
                  </td>
                  <td>
                    {sensor.lastCalibrationDate ? (
                      <TextFormat type="date" value={sensor.lastCalibrationDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{sensor.manufacturer}</td>
                  <td>{sensor.model}</td>
                  <td>{sensor.note}</td>
                  <td>{sensor.room ? <Link to={`/room/${sensor.room.id}`}>{sensor.room.name}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/sensor/${sensor.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      {canManageSensors && (
                        <>
                          <Button tag={Link} to={`/sensor/${sensor.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                            <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                          </Button>
                          <Button
                            onClick={() => (window.location.href = `/sensor/${sensor.id}/delete`)}
                            color="danger"
                            size="sm"
                            data-cy="entityDeleteButton"
                          >
                            <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                          </Button>
                        </>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">No Sensors found</div>
        )}
      </div>
    </div>
  );
};

export default Sensor;
