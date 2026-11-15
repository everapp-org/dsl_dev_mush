import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Button, Modal, ModalHeader, ModalBody, ModalFooter, Form, FormGroup, Label, Input } from 'reactstrap';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntity, receiveSupplyLine } from './supply-order-line.reducer';

export const SupplyOrderLineReceiveDialog = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [quantityReceived, setQuantityReceived] = useState<number>(0);

  const supplyOrderLineEntity = useAppSelector(state => state.supplyOrderLine.entity);
  const updating = useAppSelector(state => state.supplyOrderLine.updating);
  const updateSuccess = useAppSelector(state => state.supplyOrderLine.updateSuccess);

  useEffect(() => {
    dispatch(getEntity(id));
  }, [dispatch, id]);

  useEffect(() => {
    if (supplyOrderLineEntity.quantityOrdered) {
      setQuantityReceived(Number(supplyOrderLineEntity.quantityOrdered));
    }
  }, [supplyOrderLineEntity]);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const handleClose = () => {
    navigate('/supply-order-line');
  };

  const confirmReceive = () => {
    dispatch(receiveSupplyLine({ id: Number(id), quantityReceived }));
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="supplyOrderLineReceiveDialogHeading">
        Receive Supply Order Line
      </ModalHeader>
      <ModalBody id="mcmsApp.supplyOrderLine.receive.question">
        <p>
          <strong>Line {supplyOrderLineEntity.lineNumber}:</strong> {supplyOrderLineEntity.itemDescription}
        </p>
        <p>
          <strong>Quantity Ordered:</strong> {supplyOrderLineEntity.quantityOrdered} {supplyOrderLineEntity.unit}
        </p>
        <Form>
          <FormGroup>
            <Label for="quantity-received">Quantity Received:</Label>
            <Input
              type="number"
              id="quantity-received"
              value={quantityReceived}
              onChange={e => setQuantityReceived(Number(e.target.value))}
              min="0"
              step="0.01"
              data-cy="quantityReceivedInput"
            />
          </FormGroup>
        </Form>
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          Cancel
        </Button>
        <Button
          color="success"
          onClick={confirmReceive}
          disabled={updating || quantityReceived <= 0}
          aria-disabled={updating || quantityReceived <= 0}
          data-cy="entityConfirmReceiveButton"
        >
          Receive
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default SupplyOrderLineReceiveDialog;
