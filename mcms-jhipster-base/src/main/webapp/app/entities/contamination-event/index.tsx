import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ContaminationEvent from './contamination-event';
import ContaminationEventDetail from './contamination-event-detail';
import ContaminationEventUpdate from './contamination-event-update';
import ContaminationEventDeleteDialog from './contamination-event-delete-dialog';

const ContaminationEventRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ContaminationEvent />} />
    <Route path="new" element={<ContaminationEventUpdate />} />
    <Route path=":id">
      <Route index element={<ContaminationEventDetail />} />
      <Route path="edit" element={<ContaminationEventUpdate />} />
      <Route path="delete" element={<ContaminationEventDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ContaminationEventRoutes;
