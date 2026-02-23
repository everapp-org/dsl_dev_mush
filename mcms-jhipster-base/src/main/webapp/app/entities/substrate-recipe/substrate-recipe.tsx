import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

import { getEntities } from './substrate-recipe.reducer';

export const SubstrateRecipe = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const substrateRecipeList = useAppSelector(state => state.substrateRecipe.entities);
  const loading = useAppSelector(state => state.substrateRecipe.loading);
  const isAdminOrManager = useAppSelector(state =>
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
      <h2 id="substrate-recipe-heading" data-cy="SubstrateRecipeHeading">
        Substrate Recipes
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          {isAdminOrManager && (
            <Link
              to="/substrate-recipe/new"
              className="btn btn-primary jh-create-entity"
              id="jh-create-entity"
              data-cy="entityCreateButton"
            >
              <FontAwesomeIcon icon="plus" />
              &nbsp; Create a new Substrate Recipe
            </Link>
          )}
        </div>
      </h2>
      <div className="table-responsive">
        {substrateRecipeList && substrateRecipeList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  Name <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('version')}>
                  Version <FontAwesomeIcon icon={getSortIconByFieldName('version')} />
                </th>
                <th className="hand" onClick={sort('baseType')}>
                  Base Type <FontAwesomeIcon icon={getSortIconByFieldName('baseType')} />
                </th>
                <th className="hand" onClick={sort('compositionDetail')}>
                  Composition Detail <FontAwesomeIcon icon={getSortIconByFieldName('compositionDetail')} />
                </th>
                <th className="hand" onClick={sort('sterilizationMethod')}>
                  Sterilization Method <FontAwesomeIcon icon={getSortIconByFieldName('sterilizationMethod')} />
                </th>
                <th className="hand" onClick={sort('moistureTargetPercent')}>
                  Moisture Target Percent <FontAwesomeIcon icon={getSortIconByFieldName('moistureTargetPercent')} />
                </th>
                <th className="hand" onClick={sort('phTarget')}>
                  Ph Target <FontAwesomeIcon icon={getSortIconByFieldName('phTarget')} />
                </th>
                <th className="hand" onClick={sort('supplementNotes')}>
                  Supplement Notes <FontAwesomeIcon icon={getSortIconByFieldName('supplementNotes')} />
                </th>
                <th className="hand" onClick={sort('active')}>
                  Active <FontAwesomeIcon icon={getSortIconByFieldName('active')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {substrateRecipeList.map((substrateRecipe, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/substrate-recipe/${substrateRecipe.id}`} color="link" size="sm">
                      {substrateRecipe.id}
                    </Button>
                  </td>
                  <td>{substrateRecipe.name}</td>
                  <td>{substrateRecipe.version}</td>
                  <td>{substrateRecipe.baseType}</td>
                  <td>{substrateRecipe.compositionDetail}</td>
                  <td>{substrateRecipe.sterilizationMethod}</td>
                  <td>{substrateRecipe.moistureTargetPercent}</td>
                  <td>{substrateRecipe.phTarget}</td>
                  <td>{substrateRecipe.supplementNotes}</td>
                  <td>{substrateRecipe.active ? 'true' : 'false'}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/substrate-recipe/${substrateRecipe.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      {isAdminOrManager && (
                        <>
                          <Button
                            tag={Link}
                            to={`/substrate-recipe/${substrateRecipe.id}/edit`}
                            color="primary"
                            size="sm"
                            data-cy="entityEditButton"
                          >
                            <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                          </Button>
                          <Button
                            onClick={() => (window.location.href = `/substrate-recipe/${substrateRecipe.id}/delete`)}
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
          !loading && <div className="alert alert-warning">No Substrate Recipes found</div>
        )}
      </div>
    </div>
  );
};

export default SubstrateRecipe;
