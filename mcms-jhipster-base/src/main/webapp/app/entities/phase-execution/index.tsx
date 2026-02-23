import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import PhaseExecution from './phase-execution';
import PhaseExecutionDetail from './phase-execution-detail';
import PhaseExecutionUpdate from './phase-execution-update';
import PhaseExecutionDeleteDialog from './phase-execution-delete-dialog';

const PhaseExecutionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<PhaseExecution />} />
    <Route path="new" element={<PhaseExecutionUpdate />} />
    <Route path=":id">
      <Route index element={<PhaseExecutionDetail />} />
      <Route path="edit" element={<PhaseExecutionUpdate />} />
      <Route path="delete" element={<PhaseExecutionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default PhaseExecutionRoutes;
