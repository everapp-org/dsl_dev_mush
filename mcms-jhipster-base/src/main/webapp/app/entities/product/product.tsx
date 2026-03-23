import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './product.reducer';
import { EmptyState } from 'app/shared/components/EmptyState';

export const Product = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const productList = useAppSelector(state => state.product.entities);
  const loading = useAppSelector(state => state.product.loading);

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

  // Feature #363: Clean URL params when navigating from other entities
  // Only keep 'sort' parameter, remove any filters from other entity lists
  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const allowedParams = ['sort'];
    const hasIrrelevantParams = Array.from(params.keys()).some(key => !allowedParams.includes(key));

    if (hasIrrelevantParams) {
      // Clean URL by keeping only the sort parameter
      const cleanURL = params.has('sort') ? `?sort=${params.get('sort')}` : `?sort=${sortState.sort},${sortState.order}`;
      navigate(`${pageLocation.pathname}${cleanURL}`, { replace: true });
    }
  }, []); // Run once on mount

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
      <h1 id="product-heading" data-cy="ProductHeading">
        Products
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading} aria-disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link to="/product/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Product
          </Link>
        </div>
      </h1>
      <div className="table-responsive">
        {productList && productList.length > 0 ? (
          <Table responsive striped hover>
            <thead>
              <tr>
                <th scope="col" className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th scope="col" className="hand" onClick={sort('code')}>
                  Code <FontAwesomeIcon icon={getSortIconByFieldName('code')} />
                </th>
                <th scope="col" className="hand" onClick={sort('name')}>
                  Name <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th scope="col" className="hand" onClick={sort('description')}>
                  Description <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                </th>
                <th scope="col" className="hand" onClick={sort('defaultGrade')}>
                  Default Grade <FontAwesomeIcon icon={getSortIconByFieldName('defaultGrade')} />
                </th>
                <th scope="col" className="hand" onClick={sort('defaultWeightKg')}>
                  Default Weight Kg <FontAwesomeIcon icon={getSortIconByFieldName('defaultWeightKg')} />
                </th>
                <th scope="col" className="hand" onClick={sort('defaultPricePerKg')}>
                  Default Price Per Kg <FontAwesomeIcon icon={getSortIconByFieldName('defaultPricePerKg')} />
                </th>
                <th scope="col" className="hand" onClick={sort('shelfLifeDays')}>
                  Shelf Life Days <FontAwesomeIcon icon={getSortIconByFieldName('shelfLifeDays')} />
                </th>
                <th scope="col" className="hand" onClick={sort('active')}>
                  Active <FontAwesomeIcon icon={getSortIconByFieldName('active')} />
                </th>
                <th scope="col" className="hand" onClick={sort('note')}>
                  Note <FontAwesomeIcon icon={getSortIconByFieldName('note')} />
                </th>
                <th scope="col">
                  Strain <FontAwesomeIcon icon="sort" />
                </th>
                <th scope="col" />
              </tr>
            </thead>
            <tbody>
              {productList.map((product, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/product/${product.id}`} color="link" size="sm">
                      {product.id}
                    </Button>
                  </td>
                  <td>{product.code}</td>
                  <td>{product.name}</td>
                  <td>{product.description}</td>
                  <td>{product.defaultGrade}</td>
                  <td>{product.defaultWeightKg}</td>
                  <td>{product.defaultPricePerKg}</td>
                  <td>{product.shelfLifeDays}</td>
                  <td>{product.active ? 'true' : 'false'}</td>
                  <td>{product.note}</td>
                  <td>{product.strain ? <Link to={`/strain/${product.strain.id}`}>{product.strain.name}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/product/${product.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                        aria-label="View"
                      >
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/product/${product.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                        aria-label="Edit"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/product/${product.id}/delete`)}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                        aria-label="Delete"
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
          !loading && <EmptyState entityName="Product" entityNamePlural="products" showCreateButton={true} createLink="/product/new" />
        )}
      </div>
    </div>
  );
};

export default Product;
