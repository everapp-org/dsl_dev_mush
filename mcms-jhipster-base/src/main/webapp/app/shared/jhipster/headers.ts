export type HeaderMessage = {
  /** Success message */
  alert?: string;
  /** Error message */
  error?: string;
  /** Info message (Feature #301) */
  info?: string;
  /** Warning message (Feature #301) */
  warning?: string;
  /** Entity id for success messages. Entity name for error messages. */
  param?: string;
};

const headerToString = (headerValue: any): string => {
  if (Array.isArray(headerValue)) {
    if (headerValue.length > 1) {
      throw new Error('Multiple header values found');
    }
    headerValue = headerValue[0];
  }
  if (typeof headerValue !== 'string') {
    throw new Error('Header value is not a string');
  }
  return headerValue;
};

const decodeHeaderValue = (headerValue: string): string => decodeURIComponent(headerValue.replace(/\+/g, ' '));

export const getMessageFromHeaders = (headers: Record<string, any>): HeaderMessage => {
  let alert: string | undefined = undefined;
  let param: string | undefined = undefined;
  let error: string | undefined = undefined;
  let info: string | undefined = undefined;
  let warning: string | undefined = undefined;
  for (const [key, value] of Object.entries(headers)) {
    if (key.toLowerCase().endsWith('-alert')) {
      alert = headerToString(value);
    } else if (key.toLowerCase().endsWith('-error')) {
      error = headerToString(value);
    } else if (key.toLowerCase().endsWith('-info')) {
      info = headerToString(value);
    } else if (key.toLowerCase().endsWith('-warning')) {
      warning = headerToString(value);
    } else if (key.toLowerCase().endsWith('-params')) {
      param = decodeHeaderValue(headerToString(value));
    }
  }
  return { alert, error, info, warning, param };
};
