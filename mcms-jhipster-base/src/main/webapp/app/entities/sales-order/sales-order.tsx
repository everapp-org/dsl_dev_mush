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

import { getEntities } from './sales-order.reducer';

export const SalesOrder = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const salesOrderList = useAppSelector(state => state.salesOrder.entities);
  const loading = useAppSelector(state => state.salesOrder.loading);

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
      <h2 id="sales-order-heading" data-cy="SalesOrderHeading">
        Sales Orders
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link to="/sales-order/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Sales Order
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {salesOrderList && salesOrderList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('orderCode')}>
                  Order Code <FontAwesomeIcon icon={getSortIconByFieldName('orderCode')} />
                </th>
                <th className="hand" onClick={sort('orderDate')}>
                  Order Date <FontAwesomeIcon icon={getSortIconByFieldName('orderDate')} />
                </th>
                <th className="hand" onClick={sort('requestedDeliveryDate')}>
                  Requested Delivery Date <FontAwesomeIcon icon={getSortIconByFieldName('requestedDeliveryDate')} />
                </th>
                <th className="hand" onClick={sort('actualDeliveryDate')}>
                  Actual Delivery Date <FontAwesomeIcon icon={getSortIconByFieldName('actualDeliveryDate')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  Status <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('totalWeight')}>
                  Total Weight <FontAwesomeIcon icon={getSortIconByFieldName('totalWeight')} />
                </th>
                <th className="hand" onClick={sort('totalRevenue')}>
                  Total Revenue <FontAwesomeIcon icon={getSortIconByFieldName('totalRevenue')} />
                </th>
                <th className="hand" onClick={sort('currency')}>
                  Currency <FontAwesomeIcon icon={getSortIconByFieldName('currency')} />
                </th>
                <th className="hand" onClick={sort('invoiceNumber')}>
                  Invoice Number <FontAwesomeIcon icon={getSortIconByFieldName('invoiceNumber')} />
                </th>
                <th className="hand" onClick={sort('paymentStatus')}>
                  Payment Status <FontAwesomeIcon icon={getSortIconByFieldName('paymentStatus')} />
                </th>
                <th className="hand" onClick={sort('paymentDueDate')}>
                  Payment Due Date <FontAwesomeIcon icon={getSortIconByFieldName('paymentDueDate')} />
                </th>
                <th className="hand" onClick={sort('paymentReceivedDate')}>
                  Payment Received Date <FontAwesomeIcon icon={getSortIconByFieldName('paymentReceivedDate')} />
                </th>
                <th className="hand" onClick={sort('shippingAddress')}>
                  Shipping Address <FontAwesomeIcon icon={getSortIconByFieldName('shippingAddress')} />
                </th>
                <th className="hand" onClick={sort('note')}>
                  Note <FontAwesomeIcon icon={getSortIconByFieldName('note')} />
                </th>
                <th>
                  Customer <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {salesOrderList.map((salesOrder, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/sales-order/${salesOrder.id}`} color="link" size="sm">
                      {salesOrder.id}
                    </Button>
                  </td>
                  <td>{salesOrder.orderCode}</td>
                  <td>
                    {salesOrder.orderDate ? <TextFormat type="date" value={salesOrder.orderDate} format={APP_LOCAL_DATE_FORMAT} /> : null}
                  </td>
                  <td>
                    {salesOrder.requestedDeliveryDate ? (
                      <TextFormat type="date" value={salesOrder.requestedDeliveryDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {salesOrder.actualDeliveryDate ? (
                      <TextFormat type="date" value={salesOrder.actualDeliveryDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{salesOrder.status}</td>
                  <td>{salesOrder.totalWeight}</td>
                  <td>{salesOrder.totalRevenue}</td>
                  <td>{salesOrder.currency}</td>
                  <td>{salesOrder.invoiceNumber}</td>
                  <td>{salesOrder.paymentStatus}</td>
                  <td>
                    {salesOrder.paymentDueDate ? (
                      <TextFormat type="date" value={salesOrder.paymentDueDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {salesOrder.paymentReceivedDate ? (
                      <TextFormat type="date" value={salesOrder.paymentReceivedDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{salesOrder.shippingAddress}</td>
                  <td>{salesOrder.note}</td>
                  <td>{salesOrder.customer ? <Link to={`/customer/${salesOrder.customer.id}`}>{salesOrder.customer.name}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/sales-order/${salesOrder.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button tag={Link} to={`/sales-order/${salesOrder.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/sales-order/${salesOrder.id}/delete`)}
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
          !loading && <div className="alert alert-warning">No Sales Orders found</div>
        )}
      </div>
    </div>
  );
};

export default SalesOrder;
