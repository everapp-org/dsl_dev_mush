/**
 * Feature #278: Accessible Validated Input Component
 *
 * Enhanced version of react-jhipster's ValidatedInput with proper ARIA attributes
 * for accessibility. This component ensures validation errors are properly linked
 * to inputs using aria-invalid and aria-describedby.
 */

import React from 'react';
import { Input, FormFeedback } from 'reactstrap';
import { ValidatedInputProps } from 'react-jhipster';

/**
 * Accessible ValidatedInput with proper ARIA attributes for validation.
 *
 * This component replicates the behavior of react-jhipster's ValidatedInput
 * but adds proper ARIA attributes for accessibility compliance.
 *
 * @param ValidatedInputProps
 * @returns JSX.Element
 */
export function AccessibleValidatedInput({
  name,
  id = name,
  register,
  error,
  isTouched,
  isDirty,
  validate,
  children,
  className,
  onChange,
  onBlur,
  ...attributes
}: ValidatedInputProps): JSX.Element {
  // Generate unique ID for error message
  const errorId = `${id}-error`;

  // If no register function, return basic input
  if (!register) {
    return (
      <Input name={name} id={id} className={className} onChange={onChange} onBlur={onBlur} {...attributes}>
        {children}
      </Input>
    );
  }

  // Build className with validation states
  let inputClassName = className || '';
  inputClassName = isTouched ? `${inputClassName} is-touched` : inputClassName;
  inputClassName = isDirty ? `${inputClassName} is-dirty` : inputClassName;

  // Register field with react-hook-form
  const { name: registeredName, onBlur: onBlurValidate, onChange: onChangeValidate, ref } = register(name, validate);

  // Build aria-describedby attribute
  const existingDescribedBy = (attributes as any)['aria-describedby'];
  let ariaDescribedBy = existingDescribedBy || '';
  if (error) {
    ariaDescribedBy = ariaDescribedBy ? `${ariaDescribedBy} ${errorId}` : errorId;
  }

  return (
    <>
      <Input
        name={registeredName}
        id={id}
        valid={isTouched && !error}
        invalid={!!error}
        innerRef={ref}
        className={inputClassName}
        onChange={e => {
          void onChangeValidate(e);
          if (onChange) {
            onChange(e);
          }
        }}
        onBlur={e => {
          void onBlurValidate(e);
          if (onBlur) {
            onBlur(e);
          }
        }}
        // ARIA attributes for accessibility
        aria-invalid={error ? 'true' : 'false'}
        aria-describedby={ariaDescribedBy || undefined}
        {...attributes}
      >
        {children}
      </Input>
      {error && (
        <FormFeedback id={errorId} className="d-block">
          {error.message}
        </FormFeedback>
      )}
    </>
  );
}

AccessibleValidatedInput.displayName = 'AccessibleValidatedInput';

export default AccessibleValidatedInput;
