/**
 * Feature #278: Accessible Validated Field Component
 *
 * Enhanced version of react-jhipster's ValidatedField with proper ARIA attributes
 * for form validation accessibility. Ensures validation errors are properly linked to inputs
 * using aria-invalid and aria-describedby attributes.
 *
 * WCAG 2.1 Compliance:
 * - Success Criterion 1.3.1 (Info and Relationships): Programmatic association between errors and inputs
 * - Success Criterion 3.3.1 (Error Identification): Errors are clearly identified
 * - Success Criterion 4.1.2 (Name, Role, Value): Proper ARIA attributes for assistive technologies
 */

import React from 'react';
import { ValidatedFieldProps } from 'react-jhipster';
import { FormGroup, Label, Col } from 'reactstrap';
import { AccessibleValidatedInput } from './AccessibleValidatedInput';

/**
 * Accessible ValidatedField component with proper ARIA attributes for validation errors.
 *
 * This component replicates react-jhipster's ValidatedField but uses AccessibleValidatedInput
 * to ensure proper ARIA attributes are applied to form inputs.
 *
 * Enhancements over standard ValidatedField:
 * - Adds aria-invalid="true" when field has validation error
 * - Adds aria-describedby linking input to error message
 * - Generates unique IDs for error messages
 * - Renders error message with proper ID for screen reader association
 *
 * This is a drop-in replacement for ValidatedField - usage is identical.
 *
 * @example
 * ```tsx
 * <AccessibleValidatedField
 *   name="email"
 *   label="Email"
 *   validate={{ required: { value: true, message: 'Email is required' } }}
 * />
 * ```
 *
 * Screen reader behavior:
 * - When field is focused: "Email, edit text, invalid, Email is required"
 * - aria-invalid="true" triggers screen reader to announce "invalid"
 * - aria-describedby links to error message for detailed error info
 */
export function AccessibleValidatedField({
  children,
  name,
  id,
  disabled,
  className,
  check,
  row,
  col,
  tag,
  label,
  labelClass,
  labelHidden,
  inputClass,
  inputTag,
  hidden,
  ...attributes
}: ValidatedFieldProps): JSX.Element {
  const input = (
    <AccessibleValidatedInput name={name} id={id} disabled={disabled} className={inputClass} hidden={hidden} tag={inputTag} {...attributes}>
      {children}
    </AccessibleValidatedInput>
  );

  const inputRow = row ? <Col {...col}>{input}</Col> : input;

  return (
    <FormGroup check={check} disabled={disabled} row={row} className={className} hidden={hidden} tag={tag}>
      {check && inputRow}
      {label && (
        <Label id={`${name}Label`} check={check} for={id} className={labelClass} hidden={labelHidden || hidden}>
          {label}
        </Label>
      )}
      {!check && inputRow}
    </FormGroup>
  );
}

AccessibleValidatedField.displayName = 'AccessibleValidatedField';

export default AccessibleValidatedField;
