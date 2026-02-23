import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { TextFormat, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';
import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './contamination-event.reducer';

export const ContaminationEvent = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const contaminationEventList = useAppSelector(state => state.contaminationEvent.entities);
  const loading = useAppSelector(state => state.contaminationEvent.loading);
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
      <h2 id="contamination-event-heading" data-cy="ContaminationEventHeading">
        Contamination Events
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link
            to="/contamination-event/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Contamination Event
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {contaminationEventList && contaminationEventList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('detectedDate')}>
                  Detected Date <FontAwesomeIcon icon={getSortIconByFieldName('detectedDate')} />
                </th>
                <th className="hand" onClick={sort('type')}>
                  Type <FontAwesomeIcon icon={getSortIconByFieldName('type')} />
                </th>
                <th className="hand" onClick={sort('severity')}>
                  Severity <FontAwesomeIcon icon={getSortIconByFieldName('severity')} />
                </th>
                <th className="hand" onClick={sort('affectedBags')}>
                  Affected Bags <FontAwesomeIcon icon={getSortIconByFieldName('affectedBags')} />
                </th>
                <th className="hand" onClick={sort('affectedPercentage')}>
                  Affected Percentage <FontAwesomeIcon icon={getSortIconByFieldName('affectedPercentage')} />
                </th>
                <th className="hand" onClick={sort('actionTaken')}>
                  Action Taken <FontAwesomeIcon icon={getSortIconByFieldName('actionTaken')} />
                </th>
                <th className="hand" onClick={sort('resolvedDate')}>
                  Resolved Date <FontAwesomeIcon icon={getSortIconByFieldName('resolvedDate')} />
                </th>
                <th className="hand" onClick={sort('lossKg')}>
                  Loss Kg <FontAwesomeIcon icon={getSortIconByFieldName('lossKg')} />
                </th>
                <th className="hand" onClick={sort('rootCauseAnalysis')}>
                  Root Cause Analysis <FontAwesomeIcon icon={getSortIconByFieldName('rootCauseAnalysis')} />
                </th>
                <th className="hand" onClick={sort('preventiveMeasures')}>
                  Preventive Measures <FontAwesomeIcon icon={getSortIconByFieldName('preventiveMeasures')} />
                </th>
                <th className="hand" onClick={sort('detectedBy')}>
                  Detected By <FontAwesomeIcon icon={getSortIconByFieldName('detectedBy')} />
                </th>
                <th className="hand" onClick={sort('photosReference')}>
                  Photos Reference <FontAwesomeIcon icon={getSortIconByFieldName('photosReference')} />
                </th>
                <th className="hand" onClick={sort('note')}>
                  Note <FontAwesomeIcon icon={getSortIconByFieldName('note')} />
                </th>
                <th>
                  Batch <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Phase Execution <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Room <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {contaminationEventList.map((contaminationEvent, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/contamination-event/${contaminationEvent.id}`} color="link" size="sm">
                      {contaminationEvent.id}
                    </Button>
                  </td>
                  <td>
                    {contaminationEvent.detectedDate ? (
                      <TextFormat type="date" value={contaminationEvent.detectedDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{contaminationEvent.type}</td>
                  <td>{contaminationEvent.severity}</td>
                  <td>{contaminationEvent.affectedBags}</td>
                  <td>{contaminationEvent.affectedPercentage}</td>
                  <td>{contaminationEvent.actionTaken}</td>
                  <td>
                    {contaminationEvent.resolvedDate ? (
                      <TextFormat type="date" value={contaminationEvent.resolvedDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{contaminationEvent.lossKg}</td>
                  <td>{contaminationEvent.rootCauseAnalysis}</td>
                  <td>{contaminationEvent.preventiveMeasures}</td>
                  <td>{contaminationEvent.detectedBy}</td>
                  <td>{contaminationEvent.photosReference}</td>
                  <td>{contaminationEvent.note}</td>
                  <td>
                    {contaminationEvent.batch ? (
                      <Link to={`/batch/${contaminationEvent.batch.id}`}>{contaminationEvent.batch.batchCode}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {contaminationEvent.phaseExecution ? (
                      <Link to={`/phase-execution/${contaminationEvent.phaseExecution.id}`}>{contaminationEvent.phaseExecution.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {contaminationEvent.room ? <Link to={`/room/${contaminationEvent.room.id}`}>{contaminationEvent.room.name}</Link> : ''}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/contamination-event/${contaminationEvent.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      {isAdminOrManager && (
                        <Button
                          tag={Link}
                          to={`/contamination-event/${contaminationEvent.id}/edit`}
                          color="primary"
                          size="sm"
                          data-cy="entityEditButton"
                        >
                          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                        </Button>
                      )}
                      {isAdminOrManager && (
                        <Button
                          onClick={() => (window.location.href = `/contamination-event/${contaminationEvent.id}/delete`)}
                          color="danger"
                          size="sm"
                          data-cy="entityDeleteButton"
                        >
                          <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                        </Button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">No Contamination Events found</div>
        )}
      </div>
    </div>
  );
};

export default ContaminationEvent;
