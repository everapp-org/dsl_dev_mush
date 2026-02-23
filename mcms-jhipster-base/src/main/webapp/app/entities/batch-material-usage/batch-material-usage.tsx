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

import { getEntities } from './batch-material-usage.reducer';

export const BatchMaterialUsage = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const batchMaterialUsageList = useAppSelector(state => state.batchMaterialUsage.entities);
  const loading = useAppSelector(state => state.batchMaterialUsage.loading);

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
      <h2 id="batch-material-usage-heading" data-cy="BatchMaterialUsageHeading">
        Batch Material Usages
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link
            to="/batch-material-usage/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Batch Material Usage
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {batchMaterialUsageList && batchMaterialUsageList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('usageDate')}>
                  Usage Date <FontAwesomeIcon icon={getSortIconByFieldName('usageDate')} />
                </th>
                <th className="hand" onClick={sort('quantityUsed')}>
                  Quantity Used <FontAwesomeIcon icon={getSortIconByFieldName('quantityUsed')} />
                </th>
                <th className="hand" onClick={sort('unit')}>
                  Unit <FontAwesomeIcon icon={getSortIconByFieldName('unit')} />
                </th>
                <th className="hand" onClick={sort('purpose')}>
                  Purpose <FontAwesomeIcon icon={getSortIconByFieldName('purpose')} />
                </th>
                <th className="hand" onClick={sort('note')}>
                  Note <FontAwesomeIcon icon={getSortIconByFieldName('note')} />
                </th>
                <th>
                  Batch <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Inventory Lot <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Material <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {batchMaterialUsageList.map((batchMaterialUsage, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/batch-material-usage/${batchMaterialUsage.id}`} color="link" size="sm">
                      {batchMaterialUsage.id}
                    </Button>
                  </td>
                  <td>
                    {batchMaterialUsage.usageDate ? (
                      <TextFormat type="date" value={batchMaterialUsage.usageDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{batchMaterialUsage.quantityUsed}</td>
                  <td>{batchMaterialUsage.unit}</td>
                  <td>{batchMaterialUsage.purpose}</td>
                  <td>{batchMaterialUsage.note}</td>
                  <td>
                    {batchMaterialUsage.batch ? (
                      <Link to={`/batch/${batchMaterialUsage.batch.id}`}>{batchMaterialUsage.batch.batchCode}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {batchMaterialUsage.inventoryLot ? (
                      <Link to={`/inventory-lot/${batchMaterialUsage.inventoryLot.id}`}>{batchMaterialUsage.inventoryLot.lotCode}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {batchMaterialUsage.material ? (
                      <Link to={`/material/${batchMaterialUsage.material.id}`}>{batchMaterialUsage.material.name}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/batch-material-usage/${batchMaterialUsage.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/batch-material-usage/${batchMaterialUsage.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/batch-material-usage/${batchMaterialUsage.id}/delete`)}
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
          !loading && <div className="alert alert-warning">No Batch Material Usages found</div>
        )}
      </div>
    </div>
  );
};

export default BatchMaterialUsage;
