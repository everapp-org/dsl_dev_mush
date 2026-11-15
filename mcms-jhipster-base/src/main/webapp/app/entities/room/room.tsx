import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table, Badge } from 'reactstrap';
import { TextFormat, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_LOCAL_DATE_FORMAT, AUTHORITIES } from 'app/config/constants';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';

import { getEntities } from './room.reducer';
import { EmptyState } from 'app/shared/components/EmptyState';

// Feature #393: Helper function to get badge color based on room status
const getRoomStatusBadgeColor = (status: string): string => {
  switch (status) {
    case 'ACTIVE':
      return 'success'; // green
    case 'MAINTENANCE':
      return 'warning'; // yellow
    case 'DISINFECTION':
      return 'warning'; // orange/yellow (Bootstrap warning is amber, closest to orange)
    case 'INACTIVE':
      return 'secondary'; // gray
    default:
      return 'secondary';
  }
};

export const Room = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const roomList = useAppSelector(state => state.room.entities);
  const loading = useAppSelector(state => state.room.loading);
  const isAdmin = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));

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
      <h1 id="room-heading" data-cy="RoomHeading">
        Rooms
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading} aria-disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          {isAdmin && (
            <Link to="/room/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
              <FontAwesomeIcon icon="plus" />
              &nbsp; Create a new Room
            </Link>
          )}
        </div>
      </h1>
      <div className="table-responsive">
        {roomList && roomList.length > 0 ? (
          <Table responsive striped hover>
            <thead>
              <tr>
                <th scope="col" className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th scope="col" className="hand" onClick={sort('name')}>
                  Name <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th scope="col" className="hand" onClick={sort('code')}>
                  Code <FontAwesomeIcon icon={getSortIconByFieldName('code')} />
                </th>
                <th scope="col" className="hand" onClick={sort('roomType')}>
                  Room Type <FontAwesomeIcon icon={getSortIconByFieldName('roomType')} />
                </th>
                <th scope="col" className="hand" onClick={sort('status')}>
                  Status <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th scope="col" className="hand" onClick={sort('capacityBags')}>
                  Capacity Bags <FontAwesomeIcon icon={getSortIconByFieldName('capacityBags')} />
                </th>
                <th scope="col" className="hand" onClick={sort('currentOccupancy')}>
                  Current Occupancy <FontAwesomeIcon icon={getSortIconByFieldName('currentOccupancy')} />
                </th>
                <th scope="col" className="hand" onClick={sort('areaSqM')}>
                  Area Sq M <FontAwesomeIcon icon={getSortIconByFieldName('areaSqM')} />
                </th>
                <th scope="col" className="hand" onClick={sort('hasHVAC')}>
                  Has HVAC <FontAwesomeIcon icon={getSortIconByFieldName('hasHVAC')} />
                </th>
                <th scope="col" className="hand" onClick={sort('hasMisting')}>
                  Has Misting <FontAwesomeIcon icon={getSortIconByFieldName('hasMisting')} />
                </th>
                <th scope="col" className="hand" onClick={sort('lastDisinfectionDate')}>
                  Last Disinfection Date <FontAwesomeIcon icon={getSortIconByFieldName('lastDisinfectionDate')} />
                </th>
                <th scope="col" className="hand" onClick={sort('note')}>
                  Note <FontAwesomeIcon icon={getSortIconByFieldName('note')} />
                </th>
                <th scope="col" />
              </tr>
            </thead>
            <tbody>
              {roomList.map((room, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/room/${room.id}`} color="link" size="sm">
                      {room.id}
                    </Button>
                  </td>
                  <td>{room.name}</td>
                  <td>{room.code}</td>
                  <td>{room.roomType}</td>
                  <td>
                    <Badge color={getRoomStatusBadgeColor(room.status)}>{room.status}</Badge>
                  </td>
                  <td>{room.capacityBags}</td>
                  <td>{room.currentOccupancy}</td>
                  <td>{room.areaSqM}</td>
                  <td>{room.hasHVAC ? 'true' : 'false'}</td>
                  <td>{room.hasMisting ? 'true' : 'false'}</td>
                  <td>
                    {room.lastDisinfectionDate ? (
                      <TextFormat type="date" value={room.lastDisinfectionDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{room.note}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/room/${room.id}`} color="info" size="sm" data-cy="entityDetailsButton" aria-label="View">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      {isAdmin && (
                        <>
                          <Button
                            tag={Link}
                            to={`/room/${room.id}/edit`}
                            color="primary"
                            size="sm"
                            data-cy="entityEditButton"
                            aria-label="Edit"
                          >
                            <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                          </Button>
                          <Button
                            onClick={() => (window.location.href = `/room/${room.id}/delete`)}
                            color="danger"
                            size="sm"
                            data-cy="entityDeleteButton"
                            aria-label="Delete"
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
          !loading && <EmptyState entityName="Room" entityNamePlural="rooms" showCreateButton={true} createLink="/room/new" />
        )}
      </div>
    </div>
  );
};

export default Room;
