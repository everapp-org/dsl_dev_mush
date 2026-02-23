import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Modal, ModalBody, ModalFooter, ModalHeader, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { acknowledgeAlert, getEntity } from './environmental-alert.reducer';

export const EnvironmentalAlertAcknowledgeDialog = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [showModal, setShowModal] = useState(true);

  const environmentalAlertEntity = useAppSelector(state => state.environmentalAlert.entity);
  const updating = useAppSelector(state => state.environmentalAlert.updating);
  const updateSuccess = useAppSelector(state => state.environmentalAlert.updateSuccess);

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const handleClose = () => {
    setShowModal(false);
    navigate('/environmental-alert');
  };

  const confirmAcknowledge = values => {
    dispatch(
      acknowledgeAlert({
        id: environmentalAlertEntity.id,
        resolutionNote: values.resolutionNote,
      }),
    );
  };

  return (
    <Modal isOpen={showModal} toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="environmentalAlertAcknowledgeDialogHeading">
        Acknowledge Environmental Alert
      </ModalHeader>
      <ValidatedForm onSubmit={confirmAcknowledge} defaultValues={{ resolutionNote: '' }}>
        <ModalBody id="mcmsApp.environmentalAlert.acknowledge.question">
          <p>
            Acknowledge alert: <strong>{environmentalAlertEntity.message}</strong>
          </p>
          <p className="text-muted">
            Severity: {environmentalAlertEntity.severity} | Parameter: {environmentalAlertEntity.parameter}
          </p>
          <ValidatedField
            label="Resolution Note"
            id="environmental-alert-resolutionNote"
            name="resolutionNote"
            data-cy="resolutionNote"
            type="textarea"
            rows="4"
            placeholder="Enter what action was taken to resolve this alert..."
            validate={{
              required: { value: true, message: 'Resolution note is required.' },
              minLength: { value: 10, message: 'Resolution note must be at least 10 characters.' },
            }}
          />
        </ModalBody>
        <ModalFooter>
          <Button color="secondary" onClick={handleClose} tabIndex={1}>
            <FontAwesomeIcon icon="ban" />
            &nbsp; Cancel
          </Button>
          <Button id="jhi-confirm-acknowledge-environmentalAlert" data-cy="entityConfirmAcknowledgeButton" color="success" type="submit">
            <FontAwesomeIcon icon="check" />
            &nbsp; Acknowledge
          </Button>
        </ModalFooter>
      </ValidatedForm>
    </Modal>
  );
};

export default EnvironmentalAlertAcknowledgeDialog;
