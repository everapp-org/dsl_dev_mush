import React, { useEffect, useRef } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { LoadingSpinner } from 'app/shared/components/LoadingSpinner';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { FormErrorSummary } from 'app/shared/error/form-error-summary';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getBatches } from 'app/entities/batch/batch.reducer';
import { getEntities as getRooms } from 'app/entities/room/room.reducer';
import { createEntity, getEntity, reset, updateEntity } from './task.reducer';

export const TaskUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const batches = useAppSelector(state => state.batch.entities);
  const rooms = useAppSelector(state => state.room.entities);
  const taskEntity = useAppSelector(state => state.task.entity);
  const loading = useAppSelector(state => state.task.loading);
  const updating = useAppSelector(state => state.task.updating);
  const updateSuccess = useAppSelector(state => state.task.updateSuccess);

  // Feature #362: Form ref for reset functionality
  const formRef = useRef<any>(null);

  const handleClose = () => {
    navigate('/task', { replace: true });
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getBatches({}));
    dispatch(getRooms({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.priority !== undefined && typeof values.priority !== 'number') {
      values.priority = Number(values.priority);
    }

    const entity = {
      ...taskEntity,
      ...values,
      batch: batches.find(it => it.id.toString() === values.batch?.toString()),
      room: rooms.find(it => it.id.toString() === values.room?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...taskEntity,
          batch: taskEntity?.batch?.id,
          room: taskEntity?.room?.id,
        };

  // Feature #362: Reset button handler
  const handleReset = () => {
    if (formRef.current) {
      formRef.current.reset(defaultValues());
    }
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h1 id="mcmsApp.task.home.createOrEditLabel" data-cy="TaskCreateUpdateHeading">
            Create or edit a Task
          </h1>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <LoadingSpinner size="large" message="Loading form..." centered minHeight="200px" />
          ) : (
            <ValidatedForm ref={formRef} defaultValues={defaultValues()} onSubmit={saveEntity}>
              <FormErrorSummary />
              {!isNew ? <ValidatedField name="id" required readOnly id="task-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Title"
                id="task-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="titleLabel">Task title</UncontrolledTooltip>
              <ValidatedField label="Description" id="task-description" name="description" data-cy="description" type="textarea" />
              <UncontrolledTooltip target="descriptionLabel">Detailed instructions</UncontrolledTooltip>
              <ValidatedField
                label="Due Date"
                id="task-dueDate"
                name="dueDate"
                data-cy="dueDate"
                type="date"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="dueDateLabel">When it&#39;s due</UncontrolledTooltip>
              <ValidatedField label="Completed" id="task-completed" name="completed" data-cy="completed" check type="checkbox" />
              <UncontrolledTooltip target="completedLabel">Is it done?</UncontrolledTooltip>
              <ValidatedField
                label="Completed Date"
                id="task-completedDate"
                name="completedDate"
                data-cy="completedDate"
                type="date"
                validate={{
                  max: { value: new Date().toISOString().split('T')[0], message: 'Date cannot be in the future' },
                }}
              />
              <UncontrolledTooltip target="completedDateLabel">When it was completed</UncontrolledTooltip>
              <ValidatedField
                label="Priority"
                id="task-priority"
                name="priority"
                data-cy="priority"
                type="text"
                validate={{
                  min: { value: 1, message: 'Priority must be 1-5' },
                  max: { value: 5, message: 'Priority must be 1-5' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <UncontrolledTooltip target="priorityLabel">Priority: 1=highest, 5=lowest</UncontrolledTooltip>
              <ValidatedField label="Assigned To" id="task-assignedTo" name="assignedTo" data-cy="assignedTo" type="text" />
              <UncontrolledTooltip target="assignedToLabel">Display name of assignee</UncontrolledTooltip>
              <ValidatedField label="Note" id="task-note" name="note" data-cy="note" type="textarea" />
              <ValidatedField id="task-batch" name="batch" data-cy="batch" label="Batch" type="select">
                <option value="" key="0">
                  Select batch...
                </option>
                {batches
                  ? batches.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.batchCode}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="task-room" name="room" data-cy="room" label="Room" type="select">
                <option value="" key="0">
                  Select room...
                </option>
                {rooms
                  ? rooms.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/task" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="secondary" id="reset-form" data-cy="entityResetButton" type="button" onClick={handleReset}>
                <FontAwesomeIcon icon="undo" />
                &nbsp; Reset
              </Button>
              &nbsp;
              <Button
                color="primary"
                id="save-entity"
                data-cy="entityCreateSaveButton"
                type="submit"
                disabled={updating}
                aria-disabled={updating}
              >
                <FontAwesomeIcon icon={updating ? 'spinner' : 'save'} spin={updating} />
                &nbsp; {updating ? 'Saving...' : 'Save'}
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default TaskUpdate;
