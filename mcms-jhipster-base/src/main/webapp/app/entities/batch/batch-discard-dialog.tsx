import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader, Label, Input, FormGroup, FormFeedback } from 'reactstrap';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { discardBatch, getEntity } from './batch.reducer';

export const BatchDiscardDialog = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);
  const [reason, setReason] = useState('');
  const [reasonError, setReasonError] = useState('');

  useEffect(() => {
    dispatch(getEntity(id));
    setLoadModal(true);
  }, []);

  const batchEntity = useAppSelector(state => state.batch.entity);
  const updateSuccess = useAppSelector(state => state.batch.updateSuccess);

  const handleClose = () => {
    navigate('/batch' + (batchEntity.id ? `/${batchEntity.id}` : ''));
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmDiscard = () => {
    if (!reason || reason.trim().length === 0) {
      setReasonError('Reason is required');
      return;
    }
    dispatch(discardBatch({ id: batchEntity.id, reason: reason.trim() }));
  };

  const handleReasonChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setReason(e.target.value);
    if (reasonError) {
      setReasonError('');
    }
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="batchDiscardDialogHeading">
        Discard Contaminated Batch
      </ModalHeader>
      <ModalBody id="mcmsApp.batch.discard.question">
        <p className="mb-3">
          Are you sure you want to discard Batch <strong>{batchEntity.batchCode}</strong>?
        </p>
        <p className="text-warning mb-3">
          <FontAwesomeIcon icon="exclamation-triangle" /> This will mark the batch as contaminated and inactive.
        </p>
        <FormGroup>
          <Label for="discard-reason">Reason for discarding (required):</Label>
          <Input
            type="text"
            id="discard-reason"
            name="reason"
            value={reason}
            onChange={handleReasonChange}
            placeholder="e.g., Trichoderma contamination detected on 40% of bags"
            invalid={!!reasonError}
            data-cy="discardReasonInput"
          />
          {reasonError && <FormFeedback>{reasonError}</FormFeedback>}
        </FormGroup>
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp; Cancel
        </Button>
        <Button id="jhi-confirm-discard-batch" data-cy="entityConfirmDiscardButton" color="danger" onClick={confirmDiscard}>
          <FontAwesomeIcon icon="trash-alt" />
          &nbsp; Discard Batch
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default BatchDiscardDialog;
