import axios, { type AxiosError, type InternalAxiosRequestConfig } from 'axios';
import { Storage } from 'react-jhipster';

const TIMEOUT = 1 * 60 * 1000;
axios.defaults.timeout = TIMEOUT;
axios.defaults.baseURL = SERVER_API_URL;

// Feature #373: Request deduplication to prevent concurrent identical requests
const pendingRequests = new Map<string, Promise<any>>();

/**
 * Generate a unique key for a request based on method, URL, and params
 */
const generateRequestKey = (config: any): string => {
  const { method = 'get', url = '', params = {}, data = {} } = config;
  // Include method, URL, query params, and request body in the key
  return `${method?.toLowerCase()}:${url}:${JSON.stringify(params)}:${JSON.stringify(data)}`;
};

const setupAxiosInterceptors = onUnauthenticated => {
  const onRequestSuccess = (config: InternalAxiosRequestConfig) => {
    const token = Storage.local.get('jhi-authenticationToken') || Storage.session.get('jhi-authenticationToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  };

  const onResponseSuccess = response => {
    // Feature #373: Remove completed request from pending map
    if (response.config.method?.toLowerCase() === 'get') {
      const requestKey = generateRequestKey(response.config);
      pendingRequests.delete(requestKey);
    }
    return response;
  };

  const onResponseError = (err: AxiosError) => {
    // Feature #373: Remove failed request from pending map
    if (err.config && err.config.method?.toLowerCase() === 'get') {
      const requestKey = generateRequestKey(err.config);
      pendingRequests.delete(requestKey);
    }

    const status = err.status || (err.response ? err.response.status : 0);
    if (status === 401) {
      onUnauthenticated();
    }
    return Promise.reject(err);
  };

  axios.interceptors.request.use(onRequestSuccess);
  axios.interceptors.response.use(onResponseSuccess, onResponseError);
};

// Feature #373: Wrap axios methods to deduplicate concurrent identical GET requests
const originalGet = axios.get;
axios.get = function (url: string, config?: any) {
  const requestKey = generateRequestKey({ method: 'get', url, params: config?.params, data: config?.data });

  // If an identical GET request is already in flight, return the cached promise
  if (pendingRequests.has(requestKey)) {
    return pendingRequests.get(requestKey);
  }

  // Make the request and cache the promise
  const requestPromise = originalGet.call(this, url, config).finally(() => {
    // Clean up after request completes (success or error)
    pendingRequests.delete(requestKey);
  });

  pendingRequests.set(requestKey, requestPromise);
  return requestPromise;
};

export default setupAxiosInterceptors;
