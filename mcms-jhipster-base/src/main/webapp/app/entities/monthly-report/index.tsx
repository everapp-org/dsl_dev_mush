import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import MonthlyReport from './monthly-report';
import MonthlyReportDetail from './monthly-report-detail';
import MonthlyReportUpdate from './monthly-report-update';
import MonthlyReportDeleteDialog from './monthly-report-delete-dialog';

const MonthlyReportRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<MonthlyReport />} />
    <Route path="new" element={<MonthlyReportUpdate />} />
    <Route path=":id">
      <Route index element={<MonthlyReportDetail />} />
      <Route path="edit" element={<MonthlyReportUpdate />} />
      <Route path="delete" element={<MonthlyReportDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default MonthlyReportRoutes;
