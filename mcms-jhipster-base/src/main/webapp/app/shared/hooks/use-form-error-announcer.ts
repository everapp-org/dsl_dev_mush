/**
 * Feature #271: Form Error Announcer Hook
 *
 * React hook that monitors form validation errors and announces them to screen readers.
 * Integrates with react-hook-form to provide accessible error feedback.
 */

import { useEffect, useRef } from 'react';
import { FieldErrors } from 'react-hook-form';
import { announceFormErrors } from '../util/screen-reader-announcer';

/**
 * Hook that announces form validation errors to screen readers.
 *
 * Usage:
 * ```tsx
 * const { handleSubmit, formState: { errors } } = useForm();
 * useFormErrorAnnouncer(errors);
 * ```
 *
 * @param errors - Form errors from react-hook-form
 */
export const useFormErrorAnnouncer = (errors: FieldErrors): void => {
  const previousErrorCount = useRef(0);

  useEffect(() => {
    const currentErrorCount = Object.keys(errors).length;

    // Only announce when new errors appear or error count increases
    if (currentErrorCount > 0 && currentErrorCount > previousErrorCount.current) {
      // Convert FieldErrors to simple string map
      const errorMessages: Record<string, string> = {};

      Object.keys(errors).forEach(fieldName => {
        const error = errors[fieldName];
        if (error?.message) {
          errorMessages[fieldName] = error.message as string;
        } else if (error?.type) {
          // Fallback to error type if no message
          const errorType = typeof error.type === 'string' ? error.type : 'invalid';
          errorMessages[fieldName] = `${fieldName} is ${errorType}`;
        }
      });

      announceFormErrors(errorMessages);
    }

    previousErrorCount.current = currentErrorCount;
  }, [errors]);
};
