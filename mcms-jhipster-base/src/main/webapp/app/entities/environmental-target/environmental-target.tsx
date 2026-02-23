import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './environmental-target.reducer';

export const EnvironmentalTarget = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const environmentalTargetList = useAppSelector(state => state.environmentalTarget.entities);
  const loading = useAppSelector(state => state.environmentalTarget.loading);

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
      <h2 id="environmental-target-heading" data-cy="EnvironmentalTargetHeading">
        Environmental Targets
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link
            to="/environmental-target/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Environmental Target
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {environmentalTargetList && environmentalTargetList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('phase')}>
                  Phase <FontAwesomeIcon icon={getSortIconByFieldName('phase')} />
                </th>
                <th className="hand" onClick={sort('tempMinC')}>
                  Temp Min C <FontAwesomeIcon icon={getSortIconByFieldName('tempMinC')} />
                </th>
                <th className="hand" onClick={sort('tempMaxC')}>
                  Temp Max C <FontAwesomeIcon icon={getSortIconByFieldName('tempMaxC')} />
                </th>
                <th className="hand" onClick={sort('humidityMinPercent')}>
                  Humidity Min Percent <FontAwesomeIcon icon={getSortIconByFieldName('humidityMinPercent')} />
                </th>
                <th className="hand" onClick={sort('humidityMaxPercent')}>
                  Humidity Max Percent <FontAwesomeIcon icon={getSortIconByFieldName('humidityMaxPercent')} />
                </th>
                <th className="hand" onClick={sort('co2MaxPpm')}>
                  Co 2 Max Ppm <FontAwesomeIcon icon={getSortIconByFieldName('co2MaxPpm')} />
                </th>
                <th className="hand" onClick={sort('lightLux')}>
                  Light Lux <FontAwesomeIcon icon={getSortIconByFieldName('lightLux')} />
                </th>
                <th className="hand" onClick={sort('freshAirExchangesPerHour')}>
                  Fresh Air Exchanges Per Hour <FontAwesomeIcon icon={getSortIconByFieldName('freshAirExchangesPerHour')} />
                </th>
                <th>
                  Room <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {environmentalTargetList.map((environmentalTarget, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/environmental-target/${environmentalTarget.id}`} color="link" size="sm">
                      {environmentalTarget.id}
                    </Button>
                  </td>
                  <td>{environmentalTarget.phase}</td>
                  <td>{environmentalTarget.tempMinC}</td>
                  <td>{environmentalTarget.tempMaxC}</td>
                  <td>{environmentalTarget.humidityMinPercent}</td>
                  <td>{environmentalTarget.humidityMaxPercent}</td>
                  <td>{environmentalTarget.co2MaxPpm}</td>
                  <td>{environmentalTarget.lightLux}</td>
                  <td>{environmentalTarget.freshAirExchangesPerHour}</td>
                  <td>
                    {environmentalTarget.room ? (
                      <Link to={`/room/${environmentalTarget.room.id}`}>{environmentalTarget.room.name}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/environmental-target/${environmentalTarget.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/environmental-target/${environmentalTarget.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/environmental-target/${environmentalTarget.id}/delete`)}
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
          !loading && <div className="alert alert-warning">No Environmental Targets found</div>
        )}
      </div>
    </div>
  );
};

export default EnvironmentalTarget;
