import React from 'react';
import { Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IconProp } from '@fortawesome/fontawesome-svg-core';

export interface EmptyStateProps {
  /**
   * Entity name (e.g., "Batch", "Strain", "Product")
   */
  entityName: string;
  /**
   * Plural form of entity name (e.g., "Batches", "Strains", "Products")
   */
  entityNamePlural: string;
  /**
   * Icon to display (default: 'folder-open')
   */
  icon?: IconProp;
  /**
   * Custom message (optional). If not provided, uses default message.
   */
  message?: string;
  /**
   * Show create button
   */
  showCreateButton?: boolean;
  /**
   * Create button link
   */
  createLink?: string;
  /**
   * Additional state to pass to create link
   */
  createLinkState?: any;
  /**
   * Custom create button text
   */
  createButtonText?: string;
  /**
   * Custom data-cy attribute for testing
   */
  dataCy?: string;
}

/**
 * Consistent empty state component used across entity list pages
 *
 * @example
 * // Rich empty state with create button
 * <EmptyState
 *   entityName="Batch"
 *   entityNamePlural="batches"
 *   showCreateButton
 *   createLink="/batch/new"
 * />
 *
 * @example
 * // Simple empty state without create button
 * <EmptyState entityName="Customer" entityNamePlural="customers" />
 *
 * @example
 * // Custom message and icon
 * <EmptyState
 *   entityName="Order"
 *   entityNamePlural="orders"
 *   icon="shopping-cart"
 *   message="No orders have been placed yet. Create your first order to get started!"
 *   showCreateButton
 *   createLink="/sales-order/new"
 * />
 */
export const EmptyState: React.FC<EmptyStateProps> = ({
  entityName,
  entityNamePlural,
  icon = 'folder-open',
  message,
  showCreateButton = true,
  createLink,
  createLinkState,
  createButtonText,
  dataCy = 'emptyState',
}) => {
  const defaultMessage = `There are no ${entityNamePlural} in the system yet.${
    showCreateButton ? ` Get started by creating your first ${entityName.toLowerCase()}.` : ''
  }`;
  const displayMessage = message || defaultMessage;
  const defaultCreateLink = createLink || `/${entityName.toLowerCase().replace(/\s+/g, '-')}/new`;
  const defaultCreateButtonText = createButtonText || `Create Your First ${entityName}`;

  return (
    <div className="d-flex flex-column align-items-center justify-content-center" style={{ minHeight: '300px' }} data-cy={dataCy}>
      <div className="text-center" style={{ maxWidth: '500px' }}>
        <FontAwesomeIcon icon={icon} size="3x" className="mb-3 text-muted" />
        <h4 className="mb-3">No {entityNamePlural} Found</h4>
        <p className="text-muted mb-4">{displayMessage}</p>
        {showCreateButton && (
          <Link to={defaultCreateLink} state={createLinkState} className="btn btn-primary btn-lg" data-cy="emptyStateCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; {defaultCreateButtonText}
          </Link>
        )}
      </div>
    </div>
  );
};

export default EmptyState;
