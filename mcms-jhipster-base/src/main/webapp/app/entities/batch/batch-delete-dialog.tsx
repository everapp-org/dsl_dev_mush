import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Alert, Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'reactstrap';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { deleteEntity, getEntity } from './batch.reducer';
import axios from 'axios';

export const BatchDeleteDialog = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);
  const [cascadeWarning, setCascadeWarning] = useState<string | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id));
    setLoadModal(true);
  }, []);

  const batchEntity = useAppSelector(state => state.batch.entity);
  const updateSuccess = useAppSelector(state => state.batch.updateSuccess);

  const handleClose = () => {
    navigate('/batch');
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmDelete = async (cascade = false) => {
    setIsDeleting(true);
    try {
      const url = `/api/batches/${batchEntity.id}${cascade ? '?cascade=true' : ''}`;
      await axios.delete(url);
      // Success - close dialog and refresh
      handleClose();
      window.location.reload();
    } catch (error) {
      if (error.response?.status === 409) {
        // Conflict - batch has related entities
        const warningMessage = error.response.headers['x-mcmsapp-error'] || 'Batch has related records. Use force delete to proceed.';
        setCascadeWarning(warningMessage);
      } else {
        // Other error - use standard delete action
        dispatch(deleteEntity(batchEntity.id));
      }
    } finally {
      setIsDeleting(false);
    }
  };

  const handleForceDelete = () => {
    confirmDelete(true);
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="batchDeleteDialogHeading">
        Confirm delete operation
      </ModalHeader>
      <ModalBody id="mcmsApp.batch.delete.question">
        {cascadeWarning ? (
          <div>
            <Alert color="warning">
              <h5>Warning: Cascade Delete Required</h5>
              <p>{cascadeWarning}</p>
              <p>
                <strong>This will permanently delete the batch and all related records.</strong>
              </p>
            </Alert>
            <p>Do you want to force delete Batch {batchEntity.id} and all related records?</p>
          </div>
        ) : (
          <div>Are you sure you want to delete Batch {batchEntity.id}?</div>
        )}
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp; Cancel
        </Button>
        {cascadeWarning ? (
          <Button
            id="jhi-confirm-delete-batch"
            data-cy="entityConfirmDeleteButton"
            color="danger"
            onClick={handleForceDelete}
            disabled={isDeleting} aria-disabled={isDeleting}
          >
            <FontAwesomeIcon icon="trash" />
            &nbsp; Force Delete
          </Button>
        ) : (
          <Button
            id="jhi-confirm-delete-batch"
            data-cy="entityConfirmDeleteButton"
            color="danger"
            onClick={() => confirmDelete(false)}
            disabled={isDeleting}
          >
            <FontAwesomeIcon icon="trash" />
            &nbsp; Delete
          </Button>
        )}
      </ModalFooter>
    </Modal>
  );
};

export default BatchDeleteDialog;
