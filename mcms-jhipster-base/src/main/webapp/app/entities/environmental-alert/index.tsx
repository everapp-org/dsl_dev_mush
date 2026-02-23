import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import EnvironmentalAlert from './environmental-alert';
import EnvironmentalAlertDetail from './environmental-alert-detail';
import EnvironmentalAlertUpdate from './environmental-alert-update';
import EnvironmentalAlertDeleteDialog from './environmental-alert-delete-dialog';

const EnvironmentalAlertRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<EnvironmentalAlert />} />
    <Route path="new" element={<EnvironmentalAlertUpdate />} />
    <Route path=":id">
      <Route index element={<EnvironmentalAlertDetail />} />
      <Route path="edit" element={<EnvironmentalAlertUpdate />} />
      <Route path="delete" element={<EnvironmentalAlertDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default EnvironmentalAlertRoutes;
