import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending } from '@reduxjs/toolkit';
import { ASC } from 'app/shared/util/pagination.constants';
import { cleanEntity } from 'app/shared/util/entity-utils';
import { EntityState, IQueryParams, createEntitySlice, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { IBatch, defaultValue } from 'app/shared/model/batch.model';

// Extend EntityState to include optimistic entity tracking
interface BatchState extends EntityState<IBatch> {
  optimisticEntity: IBatch | null;
  deletedEntity: IBatch | null; // Track deleted entity for rollback on error
}

const initialState: BatchState = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
  optimisticEntity: null, // Track optimistically added entity
  deletedEntity: null, // Track deleted entity for potential rollback
};

const apiUrl = 'api/batches';

// Actions

export interface IQueryParamsWithFilters extends IQueryParams {
  search?: string;
  phase?: string;
  strainId?: number;
  isActive?: boolean;
  startDateFrom?: string;
  startDateTo?: string;
}

export const getEntities = createAsyncThunk(
  'batch/fetch_entity_list',
  async ({ page, size, sort, search, phase, strainId, isActive, startDateFrom, startDateTo }: IQueryParamsWithFilters) => {
    const params = new URLSearchParams();
    if (sort) params.append('sort', sort);
    if (page !== undefined) params.append('page', page.toString());
    if (size !== undefined) params.append('size', size.toString());
    if (search) params.append('search', search);
    if (phase) params.append('phase', phase);
    if (strainId !== undefined) params.append('strainId', strainId.toString());
    if (isActive !== undefined) params.append('isActive', isActive.toString());
    if (startDateFrom) params.append('startDateFrom', startDateFrom);
    if (startDateTo) params.append('startDateTo', startDateTo);
    params.append('cacheBuster', new Date().getTime().toString());

    const requestUrl = `${apiUrl}?${params.toString()}`;
    return axios.get<IBatch[]>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

export const getEntity = createAsyncThunk(
  'batch/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<IBatch>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

export const createEntity = createAsyncThunk(
  'batch/create_entity',
  async (entity: IBatch, thunkAPI) => {
    const result = await axios.post<IBatch>(apiUrl, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError },
);

export const updateEntity = createAsyncThunk(
  'batch/update_entity',
  async (entity: IBatch, thunkAPI) => {
    const result = await axios.put<IBatch>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError },
);

export const partialUpdateEntity = createAsyncThunk(
  'batch/partial_update_entity',
  async (entity: IBatch, thunkAPI) => {
    const result = await axios.patch<IBatch>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError },
);

export const deleteEntity = createAsyncThunk(
  'batch/delete_entity',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await axios.delete<IBatch>(requestUrl);
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError },
);

export const discardBatch = createAsyncThunk(
  'batch/discard_batch',
  async ({ id, reason }: { id: string | number; reason: string }, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}/discard`;
    const result = await axios.post<IBatch>(requestUrl, { reason });
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError },
);

// slice

export const BatchSlice = createEntitySlice({
  name: 'batch',
  initialState: initialState as any,
  extraReducers(builder) {
    builder
      .addCase(getEntity.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
      })
      // Optimistic UI: Remove entity immediately on delete pending
      .addCase(deleteEntity.pending, (state, action) => {
        const batchState = state as unknown as BatchState;
        batchState.errorMessage = null;
        batchState.updateSuccess = false;
        batchState.updating = true;

        // Find and store the entity being deleted for potential rollback
        const entityId = action.meta.arg;
        const entityToDelete = batchState.entities.find(e => e.id === entityId);
        if (entityToDelete) {
          batchState.deletedEntity = entityToDelete;
          // Remove entity immediately (optimistic delete)
          state.entities = state.entities.filter(e => e.id !== entityId);
          state.totalItems = state.totalItems - 1;
        }
      })
      .addCase(deleteEntity.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
        // Clear deleted entity - delete was successful
        state.deletedEntity = null;
      })
      // Optimistic UI: Restore entity on delete failure
      .addCase(deleteEntity.rejected, (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = false;
        state.errorMessage = action.error.message;

        // Rollback: restore the deleted entity
        if (state.deletedEntity) {
          state.entities = [state.deletedEntity, ...state.entities];
          state.totalItems = state.totalItems + 1;
          state.deletedEntity = null;
        }
      })
      // Optimistic UI: Add entity immediately on create pending
      .addCase(createEntity.pending, (state, action) => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;

        // Add optimistic entity with temporary ID
        const optimisticEntity = {
          ...action.meta.arg,
          id: `temp-${Date.now()}`,
          _isOptimistic: true,
        };
        state.optimisticEntity = optimisticEntity;
        state.entities = [optimisticEntity, ...state.entities];
        state.totalItems = state.totalItems + 1;
      })
      // Optimistic UI: Replace optimistic entity with real one on success
      .addCase(createEntity.fulfilled, (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = true;
        state.entity = action.payload.data;

        // Remove optimistic entity and let getEntities fetch the real list
        if (state.optimisticEntity) {
          state.entities = state.entities.filter(e => e.id !== state.optimisticEntity.id);
          state.optimisticEntity = null;
        }
      })
      // Optimistic UI: Remove optimistic entity on failure
      .addCase(createEntity.rejected, (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = false;
        state.errorMessage = action.error.message;

        // Rollback: remove optimistic entity
        if (state.optimisticEntity) {
          state.entities = state.entities.filter(e => e.id !== state.optimisticEntity.id);
          state.totalItems = state.totalItems - 1;
          state.optimisticEntity = null;
        }
      })
      .addMatcher(isFulfilled(getEntities), (state, action) => {
        const { data, headers } = action.payload;

        return {
          ...state,
          loading: false,
          totalItems: parseInt(headers['x-total-count'], 10),
          entities: data.sort((a, b) => {
            if (!action.meta?.arg?.sort) {
              return 1;
            }
            const order = action.meta.arg.sort.split(',')[1];
            const predicate = action.meta.arg.sort.split(',')[0];
            return order === ASC ? (a[predicate] < b[predicate] ? -1 : 1) : b[predicate] < a[predicate] ? -1 : 1;
          }),
          optimisticEntity: null, // Clear optimistic entity when fresh data loads
          deletedEntity: null, // Clear deleted entity when fresh data loads
        };
      })
      .addMatcher(isFulfilled(updateEntity, partialUpdateEntity, discardBatch), (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = true;
        state.entity = action.payload.data;
      })
      .addMatcher(isPending(getEntities, getEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.loading = true;
      })
      .addMatcher(isPending(updateEntity, partialUpdateEntity, deleteEntity, discardBatch), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      });
  },
});

export const { reset } = BatchSlice.actions;

// Reducer
export default BatchSlice.reducer;
