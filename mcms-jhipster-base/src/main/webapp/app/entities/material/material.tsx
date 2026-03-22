import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './material.reducer';

export const Material = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));
  const [importing, setImporting] = useState(false);

  const materialList = useAppSelector(state => state.material.entities);
  const loading = useAppSelector(state => state.material.loading);

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

  const handleImport = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) {
      return;
    }

    setImporting(true);
    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await fetch('/api/materials/import', {
        method: 'POST',
        body: formData,
        headers: {
          Authorization: `Bearer ${localStorage.getItem('jhi-authenticationToken') || sessionStorage.getItem('jhi-authenticationToken')}`,
        },
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.detail || 'Import failed');
      }

      const result = await response.json();

      if (result.errors && result.errors.length > 0) {
        alert(`Import completed with warnings:\n\nImported: ${result.imported} materials\nErrors:\n${result.errors.join('\n')}`);
      } else {
        alert(`Successfully imported ${result.imported} materials!`);
      }

      // Refresh the list
      getAllEntities();
    } catch (error) {
      alert(`Error importing materials: ${error.message}`);
    } finally {
      setImporting(false);
      // Reset file input
      event.target.value = '';
    }
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
      <h1 id="material-heading" data-cy="MaterialHeading">
        Materials
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading} aria-disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Button
            className="me-2"
            color="success"
            disabled={importing}
            aria-disabled={importing}
            onClick={() => document.getElementById('material-import-file')?.click()}
            data-cy="entityImportButton"
          >
            <FontAwesomeIcon icon="upload" spin={importing} /> Import CSV
          </Button>
          <input
            type="file"
            id="material-import-file"
            accept=".csv"
            style={{ display: 'none' }}
            onChange={handleImport}
            aria-label="Import materials from CSV file"
          />
          <Link to="/material/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Material
          </Link>
        </div>
      </h1>
      <div className="table-responsive">
        {materialList && materialList.length > 0 ? (
          <Table responsive>
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
                <th scope="col" className="hand" onClick={sort('category')}>
                  Category <FontAwesomeIcon icon={getSortIconByFieldName('category')} />
                </th>
                <th scope="col" className="hand" onClick={sort('defaultUnit')}>
                  Default Unit <FontAwesomeIcon icon={getSortIconByFieldName('defaultUnit')} />
                </th>
                <th scope="col" className="hand" onClick={sort('minimumStockLevel')}>
                  Minimum Stock Level <FontAwesomeIcon icon={getSortIconByFieldName('minimumStockLevel')} />
                </th>
                <th scope="col" className="hand" onClick={sort('description')}>
                  Description <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                </th>
                <th scope="col" className="hand" onClick={sort('active')}>
                  Active <FontAwesomeIcon icon={getSortIconByFieldName('active')} />
                </th>
                <th scope="col" className="hand" onClick={sort('note')}>
                  Note <FontAwesomeIcon icon={getSortIconByFieldName('note')} />
                </th>
                <th scope="col" />
              </tr>
            </thead>
            <tbody>
              {materialList.map((material, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/material/${material.id}`} color="link" size="sm">
                      {material.id}
                    </Button>
                  </td>
                  <td>{material.code}</td>
                  <td>{material.name}</td>
                  <td>{material.category}</td>
                  <td>{material.defaultUnit}</td>
                  <td>{material.minimumStockLevel}</td>
                  <td>{material.description}</td>
                  <td>{material.active ? 'true' : 'false'}</td>
                  <td>{material.note}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/material/${material.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                        aria-label="View"
                      >
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/material/${material.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                        aria-label="Edit"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/material/${material.id}/delete`)}
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
          !loading && <div className="alert alert-warning">No Materials found</div>
        )}
      </div>
    </div>
  );
};

export default Material;
