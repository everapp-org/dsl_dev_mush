import React from 'react';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

interface UnsavedChangesDialogProps {
  isOpen: boolean;
  message: string;
  onConfirm: () => void;
  onCancel: () => void;
}

/**
 * Dialog component to warn users about unsaved changes
 */
export const UnsavedChangesDialog: React.FC<UnsavedChangesDialogProps> = ({ isOpen, message, onConfirm, onCancel }) => {
  return (
    <Modal isOpen={isOpen} toggle={onCancel} backdrop="static" keyboard={false}>
      <ModalHeader toggle={onCancel}>
        <FontAwesomeIcon icon="exclamation-triangle" className="text-warning" /> Unsaved Changes
      </ModalHeader>
      <ModalBody>
        <p>{message}</p>
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={onCancel} data-cy="unsavedChangesCancel">
          <FontAwesomeIcon icon="times" /> Cancel
        </Button>
        <Button color="danger" onClick={onConfirm} data-cy="unsavedChangesConfirm">
          <FontAwesomeIcon icon="check" /> Leave Page
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default UnsavedChangesDialog;
