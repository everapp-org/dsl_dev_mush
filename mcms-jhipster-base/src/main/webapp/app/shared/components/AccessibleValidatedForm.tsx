/**
 * Feature #271: Accessible Validated Form Component
 *
 * Wrapper around react-jhipster's ValidatedForm that adds screen reader announcements
 * for form validation errors. This ensures users with assistive technologies are
 * notified when validation fails.
 */

import React from 'react';
import { ValidatedForm, ValidatedFormProps } from 'react-jhipster';
import { useFormErrorAnnouncer } from '../hooks/use-form-error-announcer';
import { useFormContext } from 'react-hook-form';

/**
 * Inner component that has access to form context
 */
const FormErrorWatcher: React.FC = () => {
  const {
    formState: { errors },
  } = useFormContext();

  // Announce errors to screen readers
  useFormErrorAnnouncer(errors);

  return null;
};

/**
 * Accessible ValidatedForm component with automatic screen reader announcements.
 *
 * Drop-in replacement for ValidatedForm that adds accessibility features:
 * - Announces validation errors to screen readers via aria-live regions
 * - Properly associates error messages with form fields
 *
 * @example
 * ```tsx
 * <AccessibleValidatedForm onSubmit={saveEntity}>
 *   <ValidatedField name="email" label="Email" />
 *   <Button type="submit">Save</Button>
 * </AccessibleValidatedForm>
 * ```
 */
export const AccessibleValidatedForm: React.FC<ValidatedFormProps> = props => {
  return (
    <ValidatedForm {...props}>
      <FormErrorWatcher />
      {props.children}
    </ValidatedForm>
  );
};

export default AccessibleValidatedForm;
