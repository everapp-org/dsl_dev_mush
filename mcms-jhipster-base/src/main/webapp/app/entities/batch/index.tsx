import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Batch from './batch';
import BatchDetail from './batch-detail';
import BatchUpdate from './batch-update';
import BatchDeleteDialog from './batch-delete-dialog';

const BatchRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Batch />} />
    <Route path="new" element={<BatchUpdate />} />
    <Route path=":id">
      <Route index element={<BatchDetail />} />
      <Route path="edit" element={<BatchUpdate />} />
      <Route path="delete" element={<BatchDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default BatchRoutes;
