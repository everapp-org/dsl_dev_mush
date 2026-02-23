import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import MandatoryFieldCheck from './mandatory-field-check';
import MandatoryFieldCheckDetail from './mandatory-field-check-detail';
import MandatoryFieldCheckUpdate from './mandatory-field-check-update';
import MandatoryFieldCheckDeleteDialog from './mandatory-field-check-delete-dialog';

const MandatoryFieldCheckRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<MandatoryFieldCheck />} />
    <Route path="new" element={<MandatoryFieldCheckUpdate />} />
    <Route path=":id">
      <Route index element={<MandatoryFieldCheckDetail />} />
      <Route path="edit" element={<MandatoryFieldCheckUpdate />} />
      <Route path="delete" element={<MandatoryFieldCheckDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default MandatoryFieldCheckRoutes;
