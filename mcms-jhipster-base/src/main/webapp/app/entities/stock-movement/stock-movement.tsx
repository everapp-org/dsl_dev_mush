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

import { getEntities } from './stock-movement.reducer';

export const StockMovement = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const stockMovementList = useAppSelector(state => state.stockMovement.entities);
  const loading = useAppSelector(state => state.stockMovement.loading);

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
      <h2 id="stock-movement-heading" data-cy="StockMovementHeading">
        Stock Movements
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link to="/stock-movement/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Stock Movement
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {stockMovementList && stockMovementList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('movementDate')}>
                  Movement Date <FontAwesomeIcon icon={getSortIconByFieldName('movementDate')} />
                </th>
                <th className="hand" onClick={sort('movementType')}>
                  Movement Type <FontAwesomeIcon icon={getSortIconByFieldName('movementType')} />
                </th>
                <th className="hand" onClick={sort('quantity')}>
                  Quantity <FontAwesomeIcon icon={getSortIconByFieldName('quantity')} />
                </th>
                <th className="hand" onClick={sort('unit')}>
                  Unit <FontAwesomeIcon icon={getSortIconByFieldName('unit')} />
                </th>
                <th className="hand" onClick={sort('reference')}>
                  Reference <FontAwesomeIcon icon={getSortIconByFieldName('reference')} />
                </th>
                <th className="hand" onClick={sort('reason')}>
                  Reason <FontAwesomeIcon icon={getSortIconByFieldName('reason')} />
                </th>
                <th className="hand" onClick={sort('performedBy')}>
                  Performed By <FontAwesomeIcon icon={getSortIconByFieldName('performedBy')} />
                </th>
                <th className="hand" onClick={sort('note')}>
                  Note <FontAwesomeIcon icon={getSortIconByFieldName('note')} />
                </th>
                <th>
                  Inventory Lot <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Batch <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {stockMovementList.map((stockMovement, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/stock-movement/${stockMovement.id}`} color="link" size="sm">
                      {stockMovement.id}
                    </Button>
                  </td>
                  <td>
                    {stockMovement.movementDate ? (
                      <TextFormat type="date" value={stockMovement.movementDate} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{stockMovement.movementType}</td>
                  <td>{stockMovement.quantity}</td>
                  <td>{stockMovement.unit}</td>
                  <td>{stockMovement.reference}</td>
                  <td>{stockMovement.reason}</td>
                  <td>{stockMovement.performedBy}</td>
                  <td>{stockMovement.note}</td>
                  <td>
                    {stockMovement.inventoryLot ? (
                      <Link to={`/inventory-lot/${stockMovement.inventoryLot.id}`}>{stockMovement.inventoryLot.lotCode}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>{stockMovement.batch ? <Link to={`/batch/${stockMovement.batch.id}`}>{stockMovement.batch.batchCode}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/stock-movement/${stockMovement.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/stock-movement/${stockMovement.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/stock-movement/${stockMovement.id}/delete`)}
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
          !loading && <div className="alert alert-warning">No Stock Movements found</div>
        )}
      </div>
    </div>
  );
};

export default StockMovement;
