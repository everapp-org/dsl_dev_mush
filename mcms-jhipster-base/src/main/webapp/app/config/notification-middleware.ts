import { toast } from 'react-toastify';
import { isFulfilledAction, isRejectedAction } from 'app/shared/reducers/reducer.utils';
import { isAxiosError } from 'axios';
import { FieldErrorVM, isProblemWithMessage } from 'app/shared/jhipster/problem-details';
import { getMessageFromHeaders } from 'app/shared/jhipster/headers';

type ToastMessage = {
  message?: string;
};

const addErrorAlert = (message: ToastMessage) => {
  toast.error(message.message, {
    autoClose: false, // Error toasts stay until manually dismissed (Feature #296)
  });
};

const addInfoAlert = (message: ToastMessage) => {
  toast.info(message.message, {
    autoClose: 5000, // Info toasts auto-dismiss after 5 seconds (Feature #301)
  });
};

const addWarningAlert = (message: ToastMessage) => {
  toast.warning(message.message, {
    autoClose: 5000, // Warning toasts auto-dismiss after 5 seconds (Feature #301)
  });
};

const getFieldErrorsToasts = (fieldErrors: FieldErrorVM[]): ToastMessage[] =>
  fieldErrors.map(fieldError => {
    if (['Min', 'Max', 'DecimalMin', 'DecimalMax'].includes(fieldError.message)) {
      fieldError.message = 'Size';
    }
    // convert 'something[14].other[4].id' to 'something[].other[].id' so translations can be written to it
    const convertedField = fieldError.field.replace(/\[\d*\]/g, '[]');
    const fieldName = convertedField.charAt(0).toUpperCase() + convertedField.slice(1);
    return { message: `Error on field "${fieldName}"` };
  });

export default () => next => action => {
  const { error, payload } = action;

  /**
   *
   * The notification middleware serves to add success, error, info, and warning notifications
   */
  if (isFulfilledAction(action) && payload?.headers) {
    const { alert, info, warning } = getMessageFromHeaders(payload.headers);
    if (alert) {
      toast.success(alert, {
        autoClose: 3000, // Success toasts auto-dismiss after 3 seconds (Feature #293, #294, #295)
      });
    }
    if (info) {
      addInfoAlert({ message: info });
    }
    if (warning) {
      addWarningAlert({ message: warning });
    }
  }

  if (isRejectedAction(action) && isAxiosError(error)) {
    if (error.response) {
      const { response } = error;
      if (response.status === 401) {
        // Show session expired message for 401 errors (except for login/account check endpoints)
        if (!error.config?.url?.endsWith('api/account') && !error.config?.url?.endsWith('api/authenticate')) {
          addErrorAlert({
            message: 'Session expired',
          });
        }
      } else if (error.config?.url?.endsWith('api/account') || error.config?.url?.endsWith('api/authenticate')) {
        // Ignore, authentication status check and authentication are treated differently.
      } else if (response.status === 403) {
        // Forbidden - user doesn't have permission
        addErrorAlert({
          message: "You don't have permission",
        });
      } else if (response.status === 0) {
        // connection refused, server not reachable
        addErrorAlert({
          message: 'Network error, please try again',
        });
      } else if (response.status === 404) {
        addErrorAlert({
          message: 'Not found',
        });
      } else if (response.status === 409) {
        // Optimistic locking / concurrency failure
        const entityName = error.config?.url?.split('/').pop()?.replace(/s$/, '') || 'record';
        addErrorAlert({
          message: `${entityName.charAt(0).toUpperCase() + entityName.slice(1)} was modified by another user`,
        });
      } else {
        const { data } = response;
        const problem = isProblemWithMessage(data) ? data : null;
        if (problem?.fieldErrors) {
          getFieldErrorsToasts(problem.fieldErrors).forEach(message => addErrorAlert(message));
        } else {
          const { error: toastError } = getMessageFromHeaders((response.headers as any) ?? {});
          if (toastError) {
            addErrorAlert({ message: toastError });
          } else if (typeof data === 'string' && data !== '') {
            addErrorAlert({ message: data });
          } else {
            toast.error(data?.detail ?? data?.message ?? data?.error ?? data?.title ?? 'Unknown error!', {
              autoClose: false, // Error toasts stay until manually dismissed (Feature #296)
            });
          }
        }
      }
    } else if (error.config?.url?.endsWith('api/account') && error.config?.method === 'get') {
      /* eslint-disable no-console */
      console.log('Authentication Error: Trying to access url api/account with GET.');
    } else if (error.code === 'ECONNABORTED') {
      // Timeout error
      addErrorAlert({ message: 'Request timed out, please try again' });
    } else {
      // Network error (e.g., no internet, DNS failure)
      addErrorAlert({ message: 'Network error, please try again' });
    }
  } else if (error) {
    addErrorAlert({ message: error.message ?? 'Unknown error!' });
  }

  return next(action);
};
