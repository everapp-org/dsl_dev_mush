import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './sales-order-line.reducer';

export const SalesOrderLine = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const salesOrderLineList = useAppSelector(state => state.salesOrderLine.entities);
  const loading = useAppSelector(state => state.salesOrderLine.loading);

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
      <h2 id="sales-order-line-heading" data-cy="SalesOrderLineHeading">
        Sales Order Lines
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link to="/sales-order-line/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Sales Order Line
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {salesOrderLineList && salesOrderLineList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('lineNumber')}>
                  Line Number <FontAwesomeIcon icon={getSortIconByFieldName('lineNumber')} />
                </th>
                <th className="hand" onClick={sort('weightKg')}>
                  Weight Kg <FontAwesomeIcon icon={getSortIconByFieldName('weightKg')} />
                </th>
                <th className="hand" onClick={sort('grade')}>
                  Grade <FontAwesomeIcon icon={getSortIconByFieldName('grade')} />
                </th>
                <th className="hand" onClick={sort('quantityUnits')}>
                  Quantity Units <FontAwesomeIcon icon={getSortIconByFieldName('quantityUnits')} />
                </th>
                <th className="hand" onClick={sort('unit')}>
                  Unit <FontAwesomeIcon icon={getSortIconByFieldName('unit')} />
                </th>
                <th className="hand" onClick={sort('pricePerKg')}>
                  Price Per Kg <FontAwesomeIcon icon={getSortIconByFieldName('pricePerKg')} />
                </th>
                <th className="hand" onClick={sort('lineTotal')}>
                  Line Total <FontAwesomeIcon icon={getSortIconByFieldName('lineTotal')} />
                </th>
                <th className="hand" onClick={sort('note')}>
                  Note <FontAwesomeIcon icon={getSortIconByFieldName('note')} />
                </th>
                <th>
                  Sales Order <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Product <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Batch <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {salesOrderLineList.map((salesOrderLine, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/sales-order-line/${salesOrderLine.id}`} color="link" size="sm">
                      {salesOrderLine.id}
                    </Button>
                  </td>
                  <td>{salesOrderLine.lineNumber}</td>
                  <td>{salesOrderLine.weightKg}</td>
                  <td>{salesOrderLine.grade}</td>
                  <td>{salesOrderLine.quantityUnits}</td>
                  <td>{salesOrderLine.unit}</td>
                  <td>{salesOrderLine.pricePerKg}</td>
                  <td>{salesOrderLine.lineTotal}</td>
                  <td>{salesOrderLine.note}</td>
                  <td>
                    {salesOrderLine.salesOrder ? (
                      <Link to={`/sales-order/${salesOrderLine.salesOrder.id}`}>{salesOrderLine.salesOrder.orderCode}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {salesOrderLine.product ? <Link to={`/product/${salesOrderLine.product.id}`}>{salesOrderLine.product.name}</Link> : ''}
                  </td>
                  <td>
                    {salesOrderLine.batch ? <Link to={`/batch/${salesOrderLine.batch.id}`}>{salesOrderLine.batch.batchCode}</Link> : ''}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/sales-order-line/${salesOrderLine.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/sales-order-line/${salesOrderLine.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/sales-order-line/${salesOrderLine.id}/delete`)}
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
          !loading && <div className="alert alert-warning">No Sales Order Lines found</div>
        )}
      </div>
    </div>
  );
};

export default SalesOrderLine;
