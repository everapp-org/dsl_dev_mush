import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import FlushCycle from './flush-cycle';
import FlushCycleDetail from './flush-cycle-detail';
import FlushCycleUpdate from './flush-cycle-update';
import FlushCycleDeleteDialog from './flush-cycle-delete-dialog';

const FlushCycleRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<FlushCycle />} />
    <Route path="new" element={<FlushCycleUpdate />} />
    <Route path=":id">
      <Route index element={<FlushCycleDetail />} />
      <Route path="edit" element={<FlushCycleUpdate />} />
      <Route path="delete" element={<FlushCycleDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default FlushCycleRoutes;
