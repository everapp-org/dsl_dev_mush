import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending } from '@reduxjs/toolkit';
import { ASC } from 'app/shared/util/pagination.constants';
import { cleanEntity } from 'app/shared/util/entity-utils';
import { EntityState, IQueryParams, createEntitySlice, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { IMonthlyReport, defaultValue } from 'app/shared/model/monthly-report.model';
import { IBatch } from 'app/shared/model/batch.model';

interface MonthlyReportState extends EntityState<IMonthlyReport> {
  batchesForReport: IBatch[];
  loadingBatches: boolean;
}

const initialState: MonthlyReportState = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
  batchesForReport: [],
  loadingBatches: false,
};

const apiUrl = 'api/monthly-reports';

// Actions

export const getEntities = createAsyncThunk(
  'monthlyReport/fetch_entity_list',
  async ({ sort }: IQueryParams) => {
    const requestUrl = `${apiUrl}?${sort ? `sort=${sort}&` : ''}cacheBuster=${new Date().getTime()}`;
    return axios.get<IMonthlyReport[]>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

export const getEntity = createAsyncThunk(
  'monthlyReport/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<IMonthlyReport>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

export const createEntity = createAsyncThunk(
  'monthlyReport/create_entity',
  async (entity: IMonthlyReport, thunkAPI) => {
    const result = await axios.post<IMonthlyReport>(apiUrl, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError },
);

export const updateEntity = createAsyncThunk(
  'monthlyReport/update_entity',
  async (entity: IMonthlyReport, thunkAPI) => {
    const result = await axios.put<IMonthlyReport>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError },
);

export const partialUpdateEntity = createAsyncThunk(
  'monthlyReport/partial_update_entity',
  async (entity: IMonthlyReport, thunkAPI) => {
    const result = await axios.patch<IMonthlyReport>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError },
);

export const deleteEntity = createAsyncThunk(
  'monthlyReport/delete_entity',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await axios.delete<IMonthlyReport>(requestUrl);
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError },
);

export const getBatchesForReport = createAsyncThunk(
  'monthlyReport/fetch_batches_for_report',
  async (reportId: string | number) => {
    const requestUrl = `${apiUrl}/${reportId}/batches`;
    return axios.get<IBatch[]>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

// slice

export const MonthlyReportSlice = createEntitySlice({
  name: 'monthlyReport',
  initialState,
  extraReducers(builder) {
    builder
      .addCase(getEntity.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
      })
      .addCase(deleteEntity.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
      })
      .addCase(getBatchesForReport.pending, state => {
        state.loadingBatches = true;
      })
      .addCase(getBatchesForReport.fulfilled, (state, action) => {
        state.loadingBatches = false;
        state.batchesForReport = action.payload.data;
      })
      .addCase(getBatchesForReport.rejected, state => {
        state.loadingBatches = false;
        state.batchesForReport = [];
      })
      .addMatcher(isFulfilled(getEntities), (state, action) => {
        const { data } = action.payload;

        return {
          ...state,
          loading: false,
          entities: data.sort((a, b) => {
            if (!action.meta?.arg?.sort) {
              return 1;
            }
            const order = action.meta.arg.sort.split(',')[1];
            const predicate = action.meta.arg.sort.split(',')[0];
            return order === ASC ? (a[predicate] < b[predicate] ? -1 : 1) : b[predicate] < a[predicate] ? -1 : 1;
          }),
        };
      })
      .addMatcher(isFulfilled(createEntity, updateEntity, partialUpdateEntity), (state, action) => {
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
      .addMatcher(isPending(createEntity, updateEntity, partialUpdateEntity, deleteEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      });
  },
});

export const { reset } = MonthlyReportSlice.actions;

// Reducer
export default MonthlyReportSlice.reducer;
