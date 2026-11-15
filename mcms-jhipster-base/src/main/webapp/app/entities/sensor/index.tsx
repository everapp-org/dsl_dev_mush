import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Sensor from './sensor';
import SensorDetail from './sensor-detail';
import SensorUpdate from './sensor-update';
import SensorDeleteDialog from './sensor-delete-dialog';

const SensorRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Sensor />} />
    <Route path="new" element={<SensorUpdate />} />
    <Route path=":id">
      <Route index element={<SensorDetail />} />
      <Route path="edit" element={<SensorUpdate />} />
      <Route path="delete" element={<SensorDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SensorRoutes;
