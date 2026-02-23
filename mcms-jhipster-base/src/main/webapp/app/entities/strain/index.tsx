import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Strain from './strain';
import StrainDetail from './strain-detail';
import StrainUpdate from './strain-update';
import StrainDeleteDialog from './strain-delete-dialog';

const StrainRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Strain />} />
    <Route path="new" element={<StrainUpdate />} />
    <Route path=":id">
      <Route index element={<StrainDetail />} />
      <Route path="edit" element={<StrainUpdate />} />
      <Route path="delete" element={<StrainDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default StrainRoutes;
