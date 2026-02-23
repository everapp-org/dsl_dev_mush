import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SalesOrderLine from './sales-order-line';
import SalesOrderLineDetail from './sales-order-line-detail';
import SalesOrderLineUpdate from './sales-order-line-update';
import SalesOrderLineDeleteDialog from './sales-order-line-delete-dialog';

const SalesOrderLineRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SalesOrderLine />} />
    <Route path="new" element={<SalesOrderLineUpdate />} />
    <Route path=":id">
      <Route index element={<SalesOrderLineDetail />} />
      <Route path="edit" element={<SalesOrderLineUpdate />} />
      <Route path="delete" element={<SalesOrderLineDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SalesOrderLineRoutes;
