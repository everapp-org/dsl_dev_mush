import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table, Input, Spinner, Row, Col, FormGroup, Label, Card, CardBody } from 'reactstrap';
import { TextFormat, getPaginationState, JhiPagination, JhiItemCount } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_LOCAL_DATE_FORMAT, AUTHORITIES } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { PhaseName } from 'app/shared/model/enumerations/phase-name.model';

import { getEntities } from './batch.reducer';
import { getEntities as getStrains } from '../strain/strain.reducer';

export const Batch = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  // Filter state
  const [filters, setFilters] = useState({
    phase: '',
    strainId: '',
    isActive: '',
    startDateFrom: '',
    startDateTo: '',
  });

  const batchList = useAppSelector(state => state.batch.entities);
  const loading = useAppSelector(state => state.batch.loading);
  const totalItems = useAppSelector(state => state.batch.totalItems);
  const strainList = useAppSelector(state => state.strain.entities);
  const canModifyBatch = useAppSelector(state =>
    hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN, AUTHORITIES.MANAGER, AUTHORITIES.OPERATOR]),
  );

  // Load strains for dropdown
  useEffect(() => {
    dispatch(getStrains({ sort: 'name,asc' }));
  }, []);

  // Initialize filters from URL on mount
  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    setFilters({
      phase: params.get('phase') || '',
      strainId: params.get('strainId') || '',
      isActive: params.get('isActive') || '',
      startDateFrom: params.get('startDateFrom') || '',
      startDateTo: params.get('startDateTo') || '',
    });
  }, []);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
        phase: filters.phase || undefined,
        strainId: filters.strainId ? parseInt(filters.strainId, 10) : undefined,
        isActive: filters.isActive ? filters.isActive === 'true' : undefined,
        startDateFrom: filters.startDateFrom || undefined,
        startDateTo: filters.startDateTo || undefined,
      }),
    );
  };

  const buildURL = () => {
    const params = new URLSearchParams();
    params.set('page', paginationState.activePage.toString());
    params.set('sort', `${paginationState.sort},${paginationState.order}`);

    if (filters.phase) params.set('phase', filters.phase);
    if (filters.strainId) params.set('strainId', filters.strainId);
    if (filters.isActive) params.set('isActive', filters.isActive);
    if (filters.startDateFrom) params.set('startDateFrom', filters.startDateFrom);
    if (filters.startDateTo) params.set('startDateTo', filters.startDateTo);

    return `?${params.toString()}`;
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = buildURL();
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort, paginationState.itemsPerPage, filters]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sortParam = params.get(SORT);
    if (page && sortParam) {
      const sortSplit = sortParam.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const handlePageSizeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newSize = parseInt(event.target.value, 10);
    setPaginationState({
      ...paginationState,
      itemsPerPage: newSize,
      activePage: 1,
    });
  };

  const handleFilterChange = (field: string) => (event: React.ChangeEvent<HTMLInputElement>) => {
    setFilters({
      ...filters,
      [field]: event.target.value,
    });
    // Reset to first page when filters change
    setPaginationState({
      ...paginationState,
      activePage: 1,
    });
  };

  const handleClearFilters = () => {
    setFilters({
      phase: '',
      strainId: '',
      isActive: '',
      startDateFrom: '',
      startDateTo: '',
    });
    setPaginationState({
      ...paginationState,
      activePage: 1,
    });
  };

  const hasActiveFilters = filters.phase || filters.strainId || filters.isActive || filters.startDateFrom || filters.startDateTo;

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="batch-heading" data-cy="BatchHeading">
        Batches
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          {canModifyBatch && (
            <Link to="/batch/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
              <FontAwesomeIcon icon="plus" />
              &nbsp; Create a new Batch
            </Link>
          )}
        </div>
      </h2>

      {/* Filter Section */}
      <Card className="mb-3">
        <CardBody>
          <Row>
            <Col md="3">
              <FormGroup>
                <Label for="phase-filter">Phase</Label>
                <Input type="select" id="phase-filter" value={filters.phase} onChange={handleFilterChange('phase')}>
                  <option value="">All Phases</option>
                  {Object.values(PhaseName).map(phase => (
                    <option key={phase} value={phase}>
                      {phase}
                    </option>
                  ))}
                </Input>
              </FormGroup>
            </Col>
            <Col md="3">
              <FormGroup>
                <Label for="strain-filter">Strain</Label>
                <Input type="select" id="strain-filter" value={filters.strainId} onChange={handleFilterChange('strainId')}>
                  <option value="">All Strains</option>
                  {strainList.map(strain => (
                    <option key={strain.id} value={strain.id}>
                      {strain.name}
                    </option>
                  ))}
                </Input>
              </FormGroup>
            </Col>
            <Col md="2">
              <FormGroup>
                <Label for="active-filter">Status</Label>
                <Input type="select" id="active-filter" value={filters.isActive} onChange={handleFilterChange('isActive')}>
                  <option value="">All</option>
                  <option value="true">Active</option>
                  <option value="false">Inactive</option>
                </Input>
              </FormGroup>
            </Col>
            <Col md="2">
              <FormGroup>
                <Label for="start-date-from">Start Date From</Label>
                <Input type="date" id="start-date-from" value={filters.startDateFrom} onChange={handleFilterChange('startDateFrom')} />
              </FormGroup>
            </Col>
            <Col md="2">
              <FormGroup>
                <Label for="start-date-to">Start Date To</Label>
                <Input type="date" id="start-date-to" value={filters.startDateTo} onChange={handleFilterChange('startDateTo')} />
              </FormGroup>
            </Col>
          </Row>
          {hasActiveFilters && (
            <Row>
              <Col>
                <Button color="secondary" size="sm" onClick={handleClearFilters}>
                  <FontAwesomeIcon icon="times" /> Clear Filters
                </Button>
              </Col>
            </Row>
          )}
        </CardBody>
      </Card>

      <div className="table-responsive">
        {loading ? (
          <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '200px' }}>
            <Spinner color="primary" style={{ width: '3rem', height: '3rem' }}>
              Loading...
            </Spinner>
          </div>
        ) : batchList && batchList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('batchCode')}>
                  Batch Code <FontAwesomeIcon icon={getSortIconByFieldName('batchCode')} />
                </th>
                <th className="hand" onClick={sort('startDate')}>
                  Start Date <FontAwesomeIcon icon={getSortIconByFieldName('startDate')} />
                </th>
                <th className="hand" onClick={sort('endDate')}>
                  End Date <FontAwesomeIcon icon={getSortIconByFieldName('endDate')} />
                </th>
                <th className="hand" onClick={sort('currentPhase')}>
                  Current Phase <FontAwesomeIcon icon={getSortIconByFieldName('currentPhase')} />
                </th>
                <th className="hand" onClick={sort('numberOfBags')}>
                  Number Of Bags <FontAwesomeIcon icon={getSortIconByFieldName('numberOfBags')} />
                </th>
                <th className="hand" onClick={sort('substrateWeightKg')}>
                  Substrate Weight Kg <FontAwesomeIcon icon={getSortIconByFieldName('substrateWeightKg')} />
                </th>
                <th className="hand" onClick={sort('spawnWeightKg')}>
                  Spawn Weight Kg <FontAwesomeIcon icon={getSortIconByFieldName('spawnWeightKg')} />
                </th>
                <th className="hand" onClick={sort('targetYieldKg')}>
                  Target Yield Kg <FontAwesomeIcon icon={getSortIconByFieldName('targetYieldKg')} />
                </th>
                <th className="hand" onClick={sort('actualTotalYieldKg')}>
                  Actual Total Yield Kg <FontAwesomeIcon icon={getSortIconByFieldName('actualTotalYieldKg')} />
                </th>
                <th className="hand" onClick={sort('biologicalEfficiencyPercent')}>
                  Biological Efficiency Percent <FontAwesomeIcon icon={getSortIconByFieldName('biologicalEfficiencyPercent')} />
                </th>
                <th className="hand" onClick={sort('isContaminated')}>
                  Is Contaminated <FontAwesomeIcon icon={getSortIconByFieldName('isContaminated')} />
                </th>
                <th className="hand" onClick={sort('isActive')}>
                  Is Active <FontAwesomeIcon icon={getSortIconByFieldName('isActive')} />
                </th>
                <th className="hand" onClick={sort('completionNote')}>
                  Completion Note <FontAwesomeIcon icon={getSortIconByFieldName('completionNote')} />
                </th>
                <th className="hand" onClick={sort('note')}>
                  Note <FontAwesomeIcon icon={getSortIconByFieldName('note')} />
                </th>
                <th>
                  Strain <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Recipe <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {batchList.map((batch, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/batch/${batch.id}`} color="link" size="sm">
                      {batch.id}
                    </Button>
                  </td>
                  <td>{batch.batchCode}</td>
                  <td>{batch.startDate ? <TextFormat type="date" value={batch.startDate} format={APP_LOCAL_DATE_FORMAT} /> : null}</td>
                  <td>{batch.endDate ? <TextFormat type="date" value={batch.endDate} format={APP_LOCAL_DATE_FORMAT} /> : null}</td>
                  <td>{batch.currentPhase}</td>
                  <td>{batch.numberOfBags}</td>
                  <td>{batch.substrateWeightKg}</td>
                  <td>{batch.spawnWeightKg}</td>
                  <td>{batch.targetYieldKg}</td>
                  <td>{batch.actualTotalYieldKg}</td>
                  <td>{batch.biologicalEfficiencyPercent}</td>
                  <td>{batch.isContaminated ? 'true' : 'false'}</td>
                  <td>{batch.isActive ? 'true' : 'false'}</td>
                  <td>{batch.completionNote}</td>
                  <td>{batch.note}</td>
                  <td>{batch.strain ? <Link to={`/strain/${batch.strain.id}`}>{batch.strain.name}</Link> : ''}</td>
                  <td>{batch.recipe ? <Link to={`/substrate-recipe/${batch.recipe.id}`}>{batch.recipe.name}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/batch/${batch.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      {canModifyBatch && (
                        <>
                          <Button tag={Link} to={`/batch/${batch.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                            <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                          </Button>
                          <Button
                            onClick={() => (window.location.href = `/batch/${batch.id}/delete`)}
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
          !loading && <div className="alert alert-warning">No Batches found</div>
        )}
      </div>
      {totalItems ? (
        <div className={batchList && batchList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex align-items-center">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} />
            <div className="ms-3">
              <span className="me-2">Items per page:</span>
              <Input
                type="select"
                value={paginationState.itemsPerPage}
                onChange={handlePageSizeChange}
                style={{ width: 'auto', display: 'inline-block' }}
              >
                <option value="10">10</option>
                <option value="20">20</option>
                <option value="50">50</option>
                <option value="100">100</option>
              </Input>
            </div>
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default Batch;
