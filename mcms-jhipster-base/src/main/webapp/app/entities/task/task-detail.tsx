import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './task.reducer';

export const TaskDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const taskEntity = useAppSelector(state => state.task.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="taskDetailsHeading">Task</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{taskEntity.id}</dd>
          <dt>
            <span id="title">Title</span>
            <UncontrolledTooltip target="title">Task title</UncontrolledTooltip>
          </dt>
          <dd>{taskEntity.title}</dd>
          <dt>
            <span id="description">Description</span>
            <UncontrolledTooltip target="description">Detailed instructions</UncontrolledTooltip>
          </dt>
          <dd>{taskEntity.description}</dd>
          <dt>
            <span id="dueDate">Due Date</span>
            <UncontrolledTooltip target="dueDate">When it&#39;s due</UncontrolledTooltip>
          </dt>
          <dd>{taskEntity.dueDate ? <TextFormat value={taskEntity.dueDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="completed">Completed</span>
            <UncontrolledTooltip target="completed">Is it done?</UncontrolledTooltip>
          </dt>
          <dd>{taskEntity.completed ? 'true' : 'false'}</dd>
          <dt>
            <span id="completedDate">Completed Date</span>
            <UncontrolledTooltip target="completedDate">When it was completed</UncontrolledTooltip>
          </dt>
          <dd>
            {taskEntity.completedDate ? <TextFormat value={taskEntity.completedDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="priority">Priority</span>
            <UncontrolledTooltip target="priority">Priority: 1=highest, 5=lowest</UncontrolledTooltip>
          </dt>
          <dd>{taskEntity.priority}</dd>
          <dt>
            <span id="assignedTo">Assigned To</span>
            <UncontrolledTooltip target="assignedTo">Display name of assignee</UncontrolledTooltip>
          </dt>
          <dd>{taskEntity.assignedTo}</dd>
          <dt>
            <span id="note">Note</span>
          </dt>
          <dd>{taskEntity.note}</dd>
          <dt>Batch</dt>
          <dd>{taskEntity.batch ? taskEntity.batch.batchCode : ''}</dd>
          <dt>Room</dt>
          <dd>{taskEntity.room ? taskEntity.room.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/task" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/task/${taskEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default TaskDetail;
