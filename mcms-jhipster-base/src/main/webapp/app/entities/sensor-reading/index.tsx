import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SensorReading from './sensor-reading';
import SensorReadingDetail from './sensor-reading-detail';
import SensorReadingUpdate from './sensor-reading-update';
import SensorReadingDeleteDialog from './sensor-reading-delete-dialog';

const SensorReadingRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SensorReading />} />
    <Route path="new" element={<SensorReadingUpdate />} />
    <Route path=":id">
      <Route index element={<SensorReadingDetail />} />
      <Route path="edit" element={<SensorReadingUpdate />} />
      <Route path="delete" element={<SensorReadingDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SensorReadingRoutes;
