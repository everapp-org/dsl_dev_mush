import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import InventoryLot from './inventory-lot';
import InventoryLotDetail from './inventory-lot-detail';
import InventoryLotUpdate from './inventory-lot-update';
import InventoryLotDeleteDialog from './inventory-lot-delete-dialog';

const InventoryLotRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<InventoryLot />} />
    <Route path="new" element={<InventoryLotUpdate />} />
    <Route path=":id">
      <Route index element={<InventoryLotDetail />} />
      <Route path="edit" element={<InventoryLotUpdate />} />
      <Route path="delete" element={<InventoryLotDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default InventoryLotRoutes;
