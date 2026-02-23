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

import { getEntities } from './supply-order-line.reducer';

export const SupplyOrderLine = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const supplyOrderLineList = useAppSelector(state => state.supplyOrderLine.entities);
  const loading = useAppSelector(state => state.supplyOrderLine.loading);

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
      <h2 id="supply-order-line-heading" data-cy="SupplyOrderLineHeading">
        Supply Order Lines
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link to="/supply-order-line/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Supply Order Line
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {supplyOrderLineList && supplyOrderLineList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('lineNumber')}>
                  Line Number <FontAwesomeIcon icon={getSortIconByFieldName('lineNumber')} />
                </th>
                <th className="hand" onClick={sort('itemDescription')}>
                  Item Description <FontAwesomeIcon icon={getSortIconByFieldName('itemDescription')} />
                </th>
                <th className="hand" onClick={sort('quantityOrdered')}>
                  Quantity Ordered <FontAwesomeIcon icon={getSortIconByFieldName('quantityOrdered')} />
                </th>
                <th className="hand" onClick={sort('quantityReceived')}>
                  Quantity Received <FontAwesomeIcon icon={getSortIconByFieldName('quantityReceived')} />
                </th>
                <th className="hand" onClick={sort('unit')}>
                  Unit <FontAwesomeIcon icon={getSortIconByFieldName('unit')} />
                </th>
                <th className="hand" onClick={sort('unitPrice')}>
                  Unit Price <FontAwesomeIcon icon={getSortIconByFieldName('unitPrice')} />
                </th>
                <th className="hand" onClick={sort('lineTotal')}>
                  Line Total <FontAwesomeIcon icon={getSortIconByFieldName('lineTotal')} />
                </th>
                <th className="hand" onClick={sort('lotNumber')}>
                  Lot Number <FontAwesomeIcon icon={getSortIconByFieldName('lotNumber')} />
                </th>
                <th className="hand" onClick={sort('expiryDate')}>
                  Expiry Date <FontAwesomeIcon icon={getSortIconByFieldName('expiryDate')} />
                </th>
                <th className="hand" onClick={sort('qualityOnReceipt')}>
                  Quality On Receipt <FontAwesomeIcon icon={getSortIconByFieldName('qualityOnReceipt')} />
                </th>
                <th className="hand" onClick={sort('isReceived')}>
                  Is Received <FontAwesomeIcon icon={getSortIconByFieldName('isReceived')} />
                </th>
                <th className="hand" onClick={sort('note')}>
                  Note <FontAwesomeIcon icon={getSortIconByFieldName('note')} />
                </th>
                <th>
                  Supply Order <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Material <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Batch <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {supplyOrderLineList.map((supplyOrderLine, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/supply-order-line/${supplyOrderLine.id}`} color="link" size="sm">
                      {supplyOrderLine.id}
                    </Button>
                  </td>
                  <td>{supplyOrderLine.lineNumber}</td>
                  <td>{supplyOrderLine.itemDescription}</td>
                  <td>{supplyOrderLine.quantityOrdered}</td>
                  <td>{supplyOrderLine.quantityReceived}</td>
                  <td>{supplyOrderLine.unit}</td>
                  <td>{supplyOrderLine.unitPrice}</td>
                  <td>{supplyOrderLine.lineTotal}</td>
                  <td>{supplyOrderLine.lotNumber}</td>
                  <td>
                    {supplyOrderLine.expiryDate ? (
                      <TextFormat type="date" value={supplyOrderLine.expiryDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{supplyOrderLine.qualityOnReceipt}</td>
                  <td>{supplyOrderLine.isReceived ? 'true' : 'false'}</td>
                  <td>{supplyOrderLine.note}</td>
                  <td>
                    {supplyOrderLine.supplyOrder ? (
                      <Link to={`/supply-order/${supplyOrderLine.supplyOrder.id}`}>{supplyOrderLine.supplyOrder.orderCode}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {supplyOrderLine.material ? (
                      <Link to={`/material/${supplyOrderLine.material.id}`}>{supplyOrderLine.material.name}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {supplyOrderLine.batch ? <Link to={`/batch/${supplyOrderLine.batch.id}`}>{supplyOrderLine.batch.batchCode}</Link> : ''}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/supply-order-line/${supplyOrderLine.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/supply-order-line/${supplyOrderLine.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/supply-order-line/${supplyOrderLine.id}/delete`)}
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
          !loading && <div className="alert alert-warning">No Supply Order Lines found</div>
        )}
      </div>
    </div>
  );
};

export default SupplyOrderLine;
