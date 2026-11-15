import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SupplyOrder from './supply-order';
import SupplyOrderDetail from './supply-order-detail';
import SupplyOrderUpdate from './supply-order-update';
import SupplyOrderDeleteDialog from './supply-order-delete-dialog';

const SupplyOrderRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SupplyOrder />} />
    <Route path="new" element={<SupplyOrderUpdate />} />
    <Route path=":id">
      <Route index element={<SupplyOrderDetail />} />
      <Route path="edit" element={<SupplyOrderUpdate />} />
      <Route path="delete" element={<SupplyOrderDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SupplyOrderRoutes;
