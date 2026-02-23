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

import { getEntities } from './inventory-lot.reducer';

export const InventoryLot = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const inventoryLotList = useAppSelector(state => state.inventoryLot.entities);
  const loading = useAppSelector(state => state.inventoryLot.loading);

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
      <h2 id="inventory-lot-heading" data-cy="InventoryLotHeading">
        Inventory Lots
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link to="/inventory-lot/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Inventory Lot
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {inventoryLotList && inventoryLotList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('lotCode')}>
                  Lot Code <FontAwesomeIcon icon={getSortIconByFieldName('lotCode')} />
                </th>
                <th className="hand" onClick={sort('receivedDate')}>
                  Received Date <FontAwesomeIcon icon={getSortIconByFieldName('receivedDate')} />
                </th>
                <th className="hand" onClick={sort('quantityReceived')}>
                  Quantity Received <FontAwesomeIcon icon={getSortIconByFieldName('quantityReceived')} />
                </th>
                <th className="hand" onClick={sort('quantityOnHand')}>
                  Quantity On Hand <FontAwesomeIcon icon={getSortIconByFieldName('quantityOnHand')} />
                </th>
                <th className="hand" onClick={sort('unit')}>
                  Unit <FontAwesomeIcon icon={getSortIconByFieldName('unit')} />
                </th>
                <th className="hand" onClick={sort('expiryDate')}>
                  Expiry Date <FontAwesomeIcon icon={getSortIconByFieldName('expiryDate')} />
                </th>
                <th className="hand" onClick={sort('storageLocation')}>
                  Storage Location <FontAwesomeIcon icon={getSortIconByFieldName('storageLocation')} />
                </th>
                <th className="hand" onClick={sort('supplierLotNumber')}>
                  Supplier Lot Number <FontAwesomeIcon icon={getSortIconByFieldName('supplierLotNumber')} />
                </th>
                <th className="hand" onClick={sort('isExhausted')}>
                  Is Exhausted <FontAwesomeIcon icon={getSortIconByFieldName('isExhausted')} />
                </th>
                <th className="hand" onClick={sort('note')}>
                  Note <FontAwesomeIcon icon={getSortIconByFieldName('note')} />
                </th>
                <th>
                  Material <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Supply Order Line <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {inventoryLotList.map((inventoryLot, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/inventory-lot/${inventoryLot.id}`} color="link" size="sm">
                      {inventoryLot.id}
                    </Button>
                  </td>
                  <td>{inventoryLot.lotCode}</td>
                  <td>
                    {inventoryLot.receivedDate ? (
                      <TextFormat type="date" value={inventoryLot.receivedDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{inventoryLot.quantityReceived}</td>
                  <td>{inventoryLot.quantityOnHand}</td>
                  <td>{inventoryLot.unit}</td>
                  <td>
                    {inventoryLot.expiryDate ? (
                      <TextFormat type="date" value={inventoryLot.expiryDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{inventoryLot.storageLocation}</td>
                  <td>{inventoryLot.supplierLotNumber}</td>
                  <td>{inventoryLot.isExhausted ? 'true' : 'false'}</td>
                  <td>{inventoryLot.note}</td>
                  <td>
                    {inventoryLot.material ? <Link to={`/material/${inventoryLot.material.id}`}>{inventoryLot.material.name}</Link> : ''}
                  </td>
                  <td>
                    {inventoryLot.supplyOrderLine ? (
                      <Link to={`/supply-order-line/${inventoryLot.supplyOrderLine.id}`}>{inventoryLot.supplyOrderLine.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/inventory-lot/${inventoryLot.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button tag={Link} to={`/inventory-lot/${inventoryLot.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/inventory-lot/${inventoryLot.id}/delete`)}
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
          !loading && <div className="alert alert-warning">No Inventory Lots found</div>
        )}
      </div>
    </div>
  );
};

export default InventoryLot;
