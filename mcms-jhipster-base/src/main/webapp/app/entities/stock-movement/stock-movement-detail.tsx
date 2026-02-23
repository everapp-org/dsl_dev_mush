import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './stock-movement.reducer';

export const StockMovementDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const stockMovementEntity = useAppSelector(state => state.stockMovement.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="stockMovementDetailsHeading">Stock Movement</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{stockMovementEntity.id}</dd>
          <dt>
            <span id="movementDate">Movement Date</span>
            <UncontrolledTooltip target="movementDate">When the movement occurred</UncontrolledTooltip>
          </dt>
          <dd>
            {stockMovementEntity.movementDate ? (
              <TextFormat value={stockMovementEntity.movementDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="movementType">Movement Type</span>
            <UncontrolledTooltip target="movementType">RECEIPT, CONSUMPTION, ADJUSTMENT, WASTE, RETURN</UncontrolledTooltip>
          </dt>
          <dd>{stockMovementEntity.movementType}</dd>
          <dt>
            <span id="quantity">Quantity</span>
            <UncontrolledTooltip target="quantity">Quantity moved (always positive; direction from type)</UncontrolledTooltip>
          </dt>
          <dd>{stockMovementEntity.quantity}</dd>
          <dt>
            <span id="unit">Unit</span>
            <UncontrolledTooltip target="unit">Unit of measure</UncontrolledTooltip>
          </dt>
          <dd>{stockMovementEntity.unit}</dd>
          <dt>
            <span id="reference">Reference</span>
            <UncontrolledTooltip target="reference">External reference (PO number, batch code, etc.)</UncontrolledTooltip>
          </dt>
          <dd>{stockMovementEntity.reference}</dd>
          <dt>
            <span id="reason">Reason</span>
            <UncontrolledTooltip target="reason">Reason for adjustment/waste</UncontrolledTooltip>
          </dt>
          <dd>{stockMovementEntity.reason}</dd>
          <dt>
            <span id="performedBy">Performed By</span>
            <UncontrolledTooltip target="performedBy">Who performed this transaction</UncontrolledTooltip>
          </dt>
          <dd>{stockMovementEntity.performedBy}</dd>
          <dt>
            <span id="note">Note</span>
          </dt>
          <dd>{stockMovementEntity.note}</dd>
          <dt>Inventory Lot</dt>
          <dd>{stockMovementEntity.inventoryLot ? stockMovementEntity.inventoryLot.lotCode : ''}</dd>
          <dt>Batch</dt>
          <dd>{stockMovementEntity.batch ? stockMovementEntity.batch.batchCode : ''}</dd>
        </dl>
        <Button tag={Link} to="/stock-movement" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/stock-movement/${stockMovementEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default StockMovementDetail;
