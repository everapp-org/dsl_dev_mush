import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SupplyOrderLine from './supply-order-line';
import SupplyOrderLineDetail from './supply-order-line-detail';
import SupplyOrderLineUpdate from './supply-order-line-update';
import SupplyOrderLineDeleteDialog from './supply-order-line-delete-dialog';
import SupplyOrderLineReceiveDialog from './supply-order-line-receive-dialog';

const SupplyOrderLineRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SupplyOrderLine />} />
    <Route path="new" element={<SupplyOrderLineUpdate />} />
    <Route path=":id">
      <Route index element={<SupplyOrderLineDetail />} />
      <Route path="edit" element={<SupplyOrderLineUpdate />} />
      <Route path="delete" element={<SupplyOrderLineDeleteDialog />} />
      <Route path="receive" element={<SupplyOrderLineReceiveDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SupplyOrderLineRoutes;
