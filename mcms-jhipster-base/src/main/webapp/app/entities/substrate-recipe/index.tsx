import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SubstrateRecipe from './substrate-recipe';
import SubstrateRecipeDetail from './substrate-recipe-detail';
import SubstrateRecipeUpdate from './substrate-recipe-update';
import SubstrateRecipeDeleteDialog from './substrate-recipe-delete-dialog';

const SubstrateRecipeRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SubstrateRecipe />} />
    <Route path="new" element={<SubstrateRecipeUpdate />} />
    <Route path=":id">
      <Route index element={<SubstrateRecipeDetail />} />
      <Route path="edit" element={<SubstrateRecipeUpdate />} />
      <Route path="delete" element={<SubstrateRecipeDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SubstrateRecipeRoutes;
