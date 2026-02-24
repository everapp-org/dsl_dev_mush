import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { AUTHORITIES } from 'app/config/constants';
import { hasAnyAuthority } from 'app/shared/auth/private-route';

import { getEntities, activateStrain, deactivateStrain } from './strain.reducer';

export const Strain = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const strainList = useAppSelector(state => state.strain.entities);
  const loading = useAppSelector(state => state.strain.loading);
  const isAdmin = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));

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

  const handleActivate = (id: number) => {
    dispatch(activateStrain(id));
  };

  const handleDeactivate = (id: number) => {
    dispatch(deactivateStrain(id));
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
      <h2 id="strain-heading" data-cy="StrainHeading">
        Strains
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link to="/strain/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Strain
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {strainList && strainList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  Name <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('species')}>
                  Species <FontAwesomeIcon icon={getSortIconByFieldName('species')} />
                </th>
                <th className="hand" onClick={sort('variety')}>
                  Variety <FontAwesomeIcon icon={getSortIconByFieldName('variety')} />
                </th>
                <th className="hand" onClick={sort('optimalTempMinC')}>
                  Optimal Temp Min C <FontAwesomeIcon icon={getSortIconByFieldName('optimalTempMinC')} />
                </th>
                <th className="hand" onClick={sort('optimalTempMaxC')}>
                  Optimal Temp Max C <FontAwesomeIcon icon={getSortIconByFieldName('optimalTempMaxC')} />
                </th>
                <th className="hand" onClick={sort('optimalHumidityMin')}>
                  Optimal Humidity Min <FontAwesomeIcon icon={getSortIconByFieldName('optimalHumidityMin')} />
                </th>
                <th className="hand" onClick={sort('optimalHumidityMax')}>
                  Optimal Humidity Max <FontAwesomeIcon icon={getSortIconByFieldName('optimalHumidityMax')} />
                </th>
                <th className="hand" onClick={sort('optimalCO2MaxPpm')}>
                  Optimal CO 2 Max Ppm <FontAwesomeIcon icon={getSortIconByFieldName('optimalCO2MaxPpm')} />
                </th>
                <th className="hand" onClick={sort('colonizationDaysMin')}>
                  Colonization Days Min <FontAwesomeIcon icon={getSortIconByFieldName('colonizationDaysMin')} />
                </th>
                <th className="hand" onClick={sort('colonizationDaysMax')}>
                  Colonization Days Max <FontAwesomeIcon icon={getSortIconByFieldName('colonizationDaysMax')} />
                </th>
                <th className="hand" onClick={sort('expectedYieldPercent')}>
                  Expected Yield Percent <FontAwesomeIcon icon={getSortIconByFieldName('expectedYieldPercent')} />
                </th>
                <th className="hand" onClick={sort('shelfLifeDays')}>
                  Shelf Life Days <FontAwesomeIcon icon={getSortIconByFieldName('shelfLifeDays')} />
                </th>
                <th className="hand" onClick={sort('note')}>
                  Note <FontAwesomeIcon icon={getSortIconByFieldName('note')} />
                </th>
                <th className="hand" onClick={sort('active')}>
                  Active <FontAwesomeIcon icon={getSortIconByFieldName('active')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {strainList.map((strain, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/strain/${strain.id}`} color="link" size="sm">
                      {strain.id}
                    </Button>
                  </td>
                  <td>{strain.name}</td>
                  <td>{strain.species}</td>
                  <td>{strain.variety}</td>
                  <td>{strain.optimalTempMinC}</td>
                  <td>{strain.optimalTempMaxC}</td>
                  <td>{strain.optimalHumidityMin}</td>
                  <td>{strain.optimalHumidityMax}</td>
                  <td>{strain.optimalCO2MaxPpm}</td>
                  <td>{strain.colonizationDaysMin}</td>
                  <td>{strain.colonizationDaysMax}</td>
                  <td>{strain.expectedYieldPercent}</td>
                  <td>{strain.shelfLifeDays}</td>
                  <td>{strain.note}</td>
                  <td>{strain.active ? 'true' : 'false'}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/strain/${strain.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button tag={Link} to={`/strain/${strain.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      {isAdmin && (
                        <>
                          {strain.active ? (
                            <Button onClick={() => handleDeactivate(strain.id)} color="warning" size="sm" data-cy="entityDeactivateButton">
                              <FontAwesomeIcon icon="ban" /> <span className="d-none d-md-inline">Deactivate</span>
                            </Button>
                          ) : (
                            <Button onClick={() => handleActivate(strain.id)} color="success" size="sm" data-cy="entityActivateButton">
                              <FontAwesomeIcon icon="check" /> <span className="d-none d-md-inline">Activate</span>
                            </Button>
                          )}
                        </>
                      )}
                      <Button
                        onClick={() => (window.location.href = `/strain/${strain.id}/delete`)}
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
          !loading && <div className="alert alert-warning">No Strains found</div>
        )}
      </div>
    </div>
  );
};

export default Strain;
