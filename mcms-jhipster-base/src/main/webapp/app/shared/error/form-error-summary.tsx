import React from 'react';
import { Alert } from 'reactstrap';
import { useFormContext } from 'react-hook-form';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

/**
 * FormErrorSummary Component
 *
 * Displays a top-level summary of all form validation errors.
 * Features:
 * - Shows error count: "3 errors found"
 * - Lists all field errors with their messages
 * - Only renders when errors exist
 * - Uses Bootstrap Alert with danger styling
 *
 * Usage: Place at the top of any ValidatedForm
 * Example:
 *   <ValidatedForm onSubmit={saveEntity}>
 *     <FormErrorSummary />
 *     <ValidatedField ... />
 *   </ValidatedForm>
 */
export const FormErrorSummary = () => {
  const {
    formState: { errors },
  } = useFormContext();

  // Get all error entries
  const errorEntries = Object.entries(errors);

  // Don't render if no errors
  if (errorEntries.length === 0) {
    return null;
  }

  // Format field names (convert camelCase to readable text)
  const formatFieldName = (fieldName: string): string => {
    return fieldName
      .replace(/([A-Z])/g, ' $1') // Add space before capital letters
      .replace(/^./, str => str.toUpperCase()) // Capitalize first letter
      .trim();
  };

  return (
    <Alert color="danger" className="mb-3" role="alert" aria-live="assertive">
      <h5 className="alert-heading">
        <FontAwesomeIcon icon="exclamation-triangle" /> {errorEntries.length} error{errorEntries.length > 1 ? 's' : ''} found
      </h5>
      <hr />
      <ul className="mb-0">
        {errorEntries.map(([fieldName, error]: [string, any]) => (
          <li key={fieldName}>
            <strong>{formatFieldName(fieldName)}:</strong> {error.message || 'Invalid value'}
          </li>
        ))}
      </ul>
    </Alert>
  );
};

export default FormErrorSummary;
