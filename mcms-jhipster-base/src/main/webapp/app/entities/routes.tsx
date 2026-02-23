import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Strain from './strain';
import SubstrateRecipe from './substrate-recipe';
import Batch from './batch';
import PhaseExecution from './phase-execution';
import FlushCycle from './flush-cycle';
import HarvestRecord from './harvest-record';
import Room from './room';
import EnvironmentalTarget from './environmental-target';
import Sensor from './sensor';
import SensorReading from './sensor-reading';
import EnvironmentalAlert from './environmental-alert';
import ContaminationEvent from './contamination-event';
import Product from './product';
import Supplier from './supplier';
import SupplyOrder from './supply-order';
import SupplyOrderLine from './supply-order-line';
import Customer from './customer';
import SalesOrder from './sales-order';
import SalesOrderLine from './sales-order-line';
import Material from './material';
import InventoryLot from './inventory-lot';
import StockMovement from './stock-movement';
import BatchMaterialUsage from './batch-material-usage';
import CostRecord from './cost-record';
import MandatoryFieldCheck from './mandatory-field-check';
import MonthlyReport from './monthly-report';
import Task from './task';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="strain/*" element={<Strain />} />
        <Route path="substrate-recipe/*" element={<SubstrateRecipe />} />
        <Route path="batch/*" element={<Batch />} />
        <Route path="phase-execution/*" element={<PhaseExecution />} />
        <Route path="flush-cycle/*" element={<FlushCycle />} />
        <Route path="harvest-record/*" element={<HarvestRecord />} />
        <Route path="room/*" element={<Room />} />
        <Route path="environmental-target/*" element={<EnvironmentalTarget />} />
        <Route path="sensor/*" element={<Sensor />} />
        <Route path="sensor-reading/*" element={<SensorReading />} />
        <Route path="environmental-alert/*" element={<EnvironmentalAlert />} />
        <Route path="contamination-event/*" element={<ContaminationEvent />} />
        <Route path="product/*" element={<Product />} />
        <Route path="supplier/*" element={<Supplier />} />
        <Route path="supply-order/*" element={<SupplyOrder />} />
        <Route path="supply-order-line/*" element={<SupplyOrderLine />} />
        <Route path="customer/*" element={<Customer />} />
        <Route path="sales-order/*" element={<SalesOrder />} />
        <Route path="sales-order-line/*" element={<SalesOrderLine />} />
        <Route path="material/*" element={<Material />} />
        <Route path="inventory-lot/*" element={<InventoryLot />} />
        <Route path="stock-movement/*" element={<StockMovement />} />
        <Route path="batch-material-usage/*" element={<BatchMaterialUsage />} />
        <Route path="cost-record/*" element={<CostRecord />} />
        <Route path="mandatory-field-check/*" element={<MandatoryFieldCheck />} />
        <Route path="monthly-report/*" element={<MonthlyReport />} />
        <Route path="task/*" element={<Task />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
