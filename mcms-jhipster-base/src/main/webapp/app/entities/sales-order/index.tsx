import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SalesOrder from './sales-order';
import SalesOrderDetail from './sales-order-detail';
import SalesOrderUpdate from './sales-order-update';
import SalesOrderDeleteDialog from './sales-order-delete-dialog';

const SalesOrderRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SalesOrder />} />
    <Route path="new" element={<SalesOrderUpdate />} />
    <Route path=":id">
      <Route index element={<SalesOrderDetail />} />
      <Route path="edit" element={<SalesOrderUpdate />} />
      <Route path="delete" element={<SalesOrderDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SalesOrderRoutes;
