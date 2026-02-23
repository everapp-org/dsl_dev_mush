import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { TextFormat, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import axios from 'axios';
import { APP_LOCAL_DATE_FORMAT, AUTHORITIES } from 'app/config/constants';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';

import { getEntities } from './task.reducer';

export const Task = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));
  const [completingTaskId, setCompletingTaskId] = useState<number | null>(null);

  const taskList = useAppSelector(state => state.task.entities);
  const loading = useAppSelector(state => state.task.loading);
  const account = useAppSelector(state => state.authentication.account);
  const isOperator = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.OPERATOR]));
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

  const handleCompleteTask = async (taskId: number) => {
    if (window.confirm('Mark this task as complete?')) {
      try {
        setCompletingTaskId(taskId);
        await axios.post(`/api/tasks/${taskId}/complete`);
        // Refresh the list to show updated status
        sortEntities();
      } catch (error) {
        console.error('Error completing task:', error);
        alert('Failed to complete task. Please try again.');
      } finally {
        setCompletingTaskId(null);
      }
    }
  };

  const canCompleteTask = (task: any) => {
    return !task.completed && (isAdminOrManager || (isOperator && task.assignedTo === account.login));
  };

  return (
    <div>
      <h2 id="task-heading" data-cy="TaskHeading">
        Tasks
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          {isAdminOrManager && (
            <Link to="/task/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
              <FontAwesomeIcon icon="plus" />
              &nbsp; Create a new Task
            </Link>
          )}
        </div>
      </h2>
      <div className="table-responsive">
        {taskList && taskList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('title')}>
                  Title <FontAwesomeIcon icon={getSortIconByFieldName('title')} />
                </th>
                <th className="hand" onClick={sort('description')}>
                  Description <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                </th>
                <th className="hand" onClick={sort('dueDate')}>
                  Due Date <FontAwesomeIcon icon={getSortIconByFieldName('dueDate')} />
                </th>
                <th className="hand" onClick={sort('completed')}>
                  Completed <FontAwesomeIcon icon={getSortIconByFieldName('completed')} />
                </th>
                <th className="hand" onClick={sort('completedDate')}>
                  Completed Date <FontAwesomeIcon icon={getSortIconByFieldName('completedDate')} />
                </th>
                <th className="hand" onClick={sort('priority')}>
                  Priority <FontAwesomeIcon icon={getSortIconByFieldName('priority')} />
                </th>
                <th className="hand" onClick={sort('assignedTo')}>
                  Assigned To <FontAwesomeIcon icon={getSortIconByFieldName('assignedTo')} />
                </th>
                <th className="hand" onClick={sort('note')}>
                  Note <FontAwesomeIcon icon={getSortIconByFieldName('note')} />
                </th>
                <th>
                  Batch <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Room <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {taskList.map((task, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/task/${task.id}`} color="link" size="sm">
                      {task.id}
                    </Button>
                  </td>
                  <td>{task.title}</td>
                  <td>{task.description}</td>
                  <td>{task.dueDate ? <TextFormat type="date" value={task.dueDate} format={APP_LOCAL_DATE_FORMAT} /> : null}</td>
                  <td>{task.completed ? 'true' : 'false'}</td>
                  <td>
                    {task.completedDate ? <TextFormat type="date" value={task.completedDate} format={APP_LOCAL_DATE_FORMAT} /> : null}
                  </td>
                  <td>{task.priority}</td>
                  <td>{task.assignedTo}</td>
                  <td>{task.note}</td>
                  <td>{task.batch ? <Link to={`/batch/${task.batch.id}`}>{task.batch.batchCode}</Link> : ''}</td>
                  <td>{task.room ? <Link to={`/room/${task.room.id}`}>{task.room.name}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/task/${task.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      {canCompleteTask(task) && (
                        <Button
                          onClick={() => handleCompleteTask(task.id)}
                          color="success"
                          size="sm"
                          disabled={completingTaskId === task.id}
                        >
                          <FontAwesomeIcon icon="check" /> <span className="d-none d-md-inline">Complete</span>
                        </Button>
                      )}
                      {isAdminOrManager && (
                        <>
                          <Button tag={Link} to={`/task/${task.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                            <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                          </Button>
                          <Button
                            onClick={() => (window.location.href = `/task/${task.id}/delete`)}
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
          !loading && <div className="alert alert-warning">No Tasks found</div>
        )}
      </div>
    </div>
  );
};

export default Task;
