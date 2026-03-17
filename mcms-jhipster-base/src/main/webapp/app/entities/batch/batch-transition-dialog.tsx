import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Button, FormGroup, Input, Label, Modal, ModalBody, ModalFooter, ModalHeader, Alert } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntity } from './batch.reducer';

// State machine allowed transitions
const ALLOWED_TRANSITIONS = {
  INOCULATION: ['EARLY_COLONIZATION', 'COMPLETED'],
  EARLY_COLONIZATION: ['FULL_COLONIZATION', 'COMPLETED'],
  FULL_COLONIZATION: ['CONSOLIDATION', 'COMPLETED'],
  CONSOLIDATION: ['FRUITING_TRIGGER', 'COMPLETED'],
  FRUITING_TRIGGER: ['PRIMORDIA', 'COMPLETED'],
  PRIMORDIA: ['FRUITING_BODY_GROWTH', 'COMPLETED'],
  FRUITING_BODY_GROWTH: ['HARVEST', 'COMPLETED'],
  HARVEST: ['REHYDRATION_PAUSE', 'COMPLETED'],
  REHYDRATION_PAUSE: ['HARVEST', 'COMPLETED'],
  COMPLETED: [],
};

export const BatchTransitionDialog = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);
  const [targetPhase, setTargetPhase] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id));
    setLoadModal(true);
  }, []);

  const batchEntity = useAppSelector(state => state.batch.entity);

  const handleClose = () => {
    navigate(`/batch/${id}`);
  };

  const confirmTransition = async () => {
    if (!targetPhase) {
      setError('Please select a target phase');
      return;
    }

    setLoading(true);
    setError('');

    try {
      await axios.post(`/api/batches/${id}/transition`, {
        targetPhase,
        adminPassword: 'dummy', // Required by DTO but not used for normal transitions
      });
      // Refresh the batch data
      dispatch(getEntity(id));
      handleClose();
    } catch (err) {
      if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError('Failed to transition batch. Please try again.');
      }
      setLoading(false);
    }
  };

  const currentPhase = batchEntity.currentPhase;
  const allowedPhases = ALLOWED_TRANSITIONS[currentPhase] || [];

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="batchTransitionDialogHeading">
        Transition Batch to Next Phase
      </ModalHeader>
      <ModalBody>
        <p>
          <strong>Batch:</strong> {batchEntity.batchCode}
          <br />
          <strong>Current Phase:</strong> {currentPhase}
        </p>

        {allowedPhases.length === 0 ? (
          <Alert color="warning">
            <strong>No transitions available.</strong>
            <br />
            {currentPhase === 'COMPLETED' ? 'This batch is completed.' : 'No valid transitions from this phase.'}
          </Alert>
        ) : (
          <FormGroup>
            <Label for="targetPhase">
              Target Phase <span className="text-danger">*</span>
            </Label>
            <Input type="select" id="targetPhase" value={targetPhase} onChange={e => setTargetPhase(e.target.value)} data-cy="targetPhase">
              <option value="">Select next phase...</option>
              {allowedPhases.map(phase => (
                <option key={phase} value={phase}>
                  {phase}
                </option>
              ))}
            </Input>
            <small className="form-text text-muted">
              Only valid state machine transitions are shown. For emergency overrides, use Force Transition (admin only).
            </small>
          </FormGroup>
        )}

        {error && <Alert color="danger">{error}</Alert>}
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose} disabled={loading} aria-disabled={loading}>
          <FontAwesomeIcon icon="ban" />
          &nbsp; Cancel
        </Button>
        {allowedPhases.length > 0 && (
          <Button
            id="jhi-confirm-transition-batch"
            data-cy="entityConfirmTransitionButton"
            color="primary"
            onClick={confirmTransition}
            disabled={loading || !targetPhase} aria-disabled={loading || !targetPhase}
          >
            <FontAwesomeIcon icon="arrow-right" />
            &nbsp; {loading ? 'Transitioning...' : 'Transition'}
          </Button>
        )}
      </ModalFooter>
    </Modal>
  );
};

export default BatchTransitionDialog;
