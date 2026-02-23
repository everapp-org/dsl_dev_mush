import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CostRecord from './cost-record';
import CostRecordDetail from './cost-record-detail';
import CostRecordUpdate from './cost-record-update';
import CostRecordDeleteDialog from './cost-record-delete-dialog';

const CostRecordRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CostRecord />} />
    <Route path="new" element={<CostRecordUpdate />} />
    <Route path=":id">
      <Route index element={<CostRecordDetail />} />
      <Route path="edit" element={<CostRecordUpdate />} />
      <Route path="delete" element={<CostRecordDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CostRecordRoutes;
