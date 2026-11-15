import strain from 'app/entities/strain/strain.reducer';
import substrateRecipe from 'app/entities/substrate-recipe/substrate-recipe.reducer';
import batch from 'app/entities/batch/batch.reducer';
import phaseExecution from 'app/entities/phase-execution/phase-execution.reducer';
import flushCycle from 'app/entities/flush-cycle/flush-cycle.reducer';
import harvestRecord from 'app/entities/harvest-record/harvest-record.reducer';
import room from 'app/entities/room/room.reducer';
import environmentalTarget from 'app/entities/environmental-target/environmental-target.reducer';
import sensor from 'app/entities/sensor/sensor.reducer';
import sensorReading from 'app/entities/sensor-reading/sensor-reading.reducer';
import environmentalAlert from 'app/entities/environmental-alert/environmental-alert.reducer';
import contaminationEvent from 'app/entities/contamination-event/contamination-event.reducer';
import product from 'app/entities/product/product.reducer';
import supplier from 'app/entities/supplier/supplier.reducer';
import supplyOrder from 'app/entities/supply-order/supply-order.reducer';
import supplyOrderLine from 'app/entities/supply-order-line/supply-order-line.reducer';
import customer from 'app/entities/customer/customer.reducer';
import salesOrder from 'app/entities/sales-order/sales-order.reducer';
import salesOrderLine from 'app/entities/sales-order-line/sales-order-line.reducer';
import material from 'app/entities/material/material.reducer';
import inventoryLot from 'app/entities/inventory-lot/inventory-lot.reducer';
import stockMovement from 'app/entities/stock-movement/stock-movement.reducer';
import batchMaterialUsage from 'app/entities/batch-material-usage/batch-material-usage.reducer';
import costRecord from 'app/entities/cost-record/cost-record.reducer';
import mandatoryFieldCheck from 'app/entities/mandatory-field-check/mandatory-field-check.reducer';
import monthlyReport from 'app/entities/monthly-report/monthly-report.reducer';
import task from 'app/entities/task/task.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  strain,
  substrateRecipe,
  batch,
  phaseExecution,
  flushCycle,
  harvestRecord,
  room,
  environmentalTarget,
  sensor,
  sensorReading,
  environmentalAlert,
  contaminationEvent,
  product,
  supplier,
  supplyOrder,
  supplyOrderLine,
  customer,
  salesOrder,
  salesOrderLine,
  material,
  inventoryLot,
  stockMovement,
  batchMaterialUsage,
  costRecord,
  mandatoryFieldCheck,
  monthlyReport,
  task,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
