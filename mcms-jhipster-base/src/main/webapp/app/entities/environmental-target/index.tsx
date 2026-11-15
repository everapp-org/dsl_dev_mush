import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import EnvironmentalTarget from './environmental-target';
import EnvironmentalTargetDetail from './environmental-target-detail';
import EnvironmentalTargetUpdate from './environmental-target-update';
import EnvironmentalTargetDeleteDialog from './environmental-target-delete-dialog';

const EnvironmentalTargetRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<EnvironmentalTarget />} />
    <Route path="new" element={<EnvironmentalTargetUpdate />} />
    <Route path=":id">
      <Route index element={<EnvironmentalTargetDetail />} />
      <Route path="edit" element={<EnvironmentalTargetUpdate />} />
      <Route path="delete" element={<EnvironmentalTargetDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default EnvironmentalTargetRoutes;
