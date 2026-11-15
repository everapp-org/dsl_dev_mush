import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import HarvestRecord from './harvest-record';
import HarvestRecordDetail from './harvest-record-detail';
import HarvestRecordUpdate from './harvest-record-update';
import HarvestRecordDeleteDialog from './harvest-record-delete-dialog';

const HarvestRecordRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<HarvestRecord />} />
    <Route path="new" element={<HarvestRecordUpdate />} />
    <Route path=":id">
      <Route index element={<HarvestRecordDetail />} />
      <Route path="edit" element={<HarvestRecordUpdate />} />
      <Route path="delete" element={<HarvestRecordDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default HarvestRecordRoutes;
