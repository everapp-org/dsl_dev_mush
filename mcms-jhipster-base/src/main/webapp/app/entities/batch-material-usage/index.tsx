import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import BatchMaterialUsage from './batch-material-usage';
import BatchMaterialUsageDetail from './batch-material-usage-detail';
import BatchMaterialUsageUpdate from './batch-material-usage-update';
import BatchMaterialUsageDeleteDialog from './batch-material-usage-delete-dialog';

const BatchMaterialUsageRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<BatchMaterialUsage />} />
    <Route path="new" element={<BatchMaterialUsageUpdate />} />
    <Route path=":id">
      <Route index element={<BatchMaterialUsageDetail />} />
      <Route path="edit" element={<BatchMaterialUsageUpdate />} />
      <Route path="delete" element={<BatchMaterialUsageDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default BatchMaterialUsageRoutes;
