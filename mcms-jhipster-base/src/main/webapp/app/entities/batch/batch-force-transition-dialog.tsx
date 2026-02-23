import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Button, FormGroup, Input, Label, Modal, ModalBody, ModalFooter, ModalHeader } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntity } from './batch.reducer';

const PHASE_OPTIONS = [
  'INOCULATION',
  'EARLY_COLONIZATION',
  'FULL_COLONIZATION',
  'CONSOLIDATION',
  'FRUITING_TRIGGER',
  'PRIMORDIA',
  'FRUITING_BODY_GROWTH',
  'HARVEST',
  'REHYDRATION_PAUSE',
  'COMPLETED',
];

export const BatchForceTransitionDialog = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);
  const [targetPhase, setTargetPhase] = useState('');
  const [adminPassword, setAdminPassword] = useState('');
  const [reason, setReason] = useState('');
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
    if (!adminPassword) {
      setError('Admin password is required');
      return;
    }

    setLoading(true);
    setError('');

    try {
      await axios.post(`/api/batches/${id}/force-transition`, {
        targetPhase,
        adminPassword,
        reason,
      });
      // Refresh the batch data
      dispatch(getEntity(id));
      handleClose();
    } catch (err) {
      if (err.response?.status === 401) {
        setError('Invalid admin password');
      } else {
        setError('Failed to force transition. Please try again.');
      }
      setLoading(false);
    }
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="batchForceTransitionDialogHeading">
        Force Batch State Transition (Admin Override)
      </ModalHeader>
      <ModalBody>
        <div className="alert alert-warning">
          <strong>Warning:</strong> This will bypass state machine guards and force the batch to any phase. Use with caution.
        </div>

        <p>
          <strong>Batch:</strong> {batchEntity.batchCode}
          <br />
          <strong>Current Phase:</strong> {batchEntity.currentPhase}
        </p>

        <FormGroup>
          <Label for="targetPhase">Target Phase</Label>
          <Input type="select" id="targetPhase" value={targetPhase} onChange={e => setTargetPhase(e.target.value)} data-cy="targetPhase">
            <option value="">Select phase...</option>
            {PHASE_OPTIONS.map(phase => (
              <option key={phase} value={phase}>
                {phase}
              </option>
            ))}
          </Input>
        </FormGroup>

        <FormGroup>
          <Label for="reason">Reason (optional)</Label>
          <Input
            type="textarea"
            id="reason"
            value={reason}
            onChange={e => setReason(e.target.value)}
            placeholder="Why is this force transition necessary?"
            data-cy="reason"
          />
        </FormGroup>

        <FormGroup>
          <Label for="adminPassword">
            Admin Password <span className="text-danger">*</span>
          </Label>
          <Input
            type="password"
            id="adminPassword"
            value={adminPassword}
            onChange={e => setAdminPassword(e.target.value)}
            placeholder="Enter your password to confirm"
            data-cy="adminPassword"
          />
        </FormGroup>

        {error && <div className="alert alert-danger">{error}</div>}
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose} disabled={loading}>
          <FontAwesomeIcon icon="ban" />
          &nbsp; Cancel
        </Button>
        <Button
          id="jhi-confirm-force-transition-batch"
          data-cy="entityConfirmForceTransitionButton"
          color="warning"
          onClick={confirmTransition}
          disabled={loading}
        >
          <FontAwesomeIcon icon="exchange-alt" />
          &nbsp; {loading ? 'Processing...' : 'Force Transition'}
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default BatchForceTransitionDialog;
