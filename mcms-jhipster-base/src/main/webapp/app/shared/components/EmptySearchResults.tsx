import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

interface EmptySearchResultsProps {
  /** True if filters/search is active, false if no data exists at all */
  hasActiveSearch: boolean;
  /** The search term or filter description (e.g., "Project Alpha" or "Active: Yes, Phase: Fruiting") */
  searchDescription?: string;
  /** Entity name (e.g., "Strains", "Batches") for display */
  entityName: string;
  /** Callback to clear search/filters */
  onClearSearch?: () => void;
  /** Optional create button props */
  createButton?: {
    to: string;
    label: string;
    visible?: boolean;
  };
  /** Optional custom suggestions */
  suggestions?: string[];
}

/**
 * Reusable component for displaying empty search results or no data states
 * with helpful suggestions for users.
 */
export const EmptySearchResults: React.FC<EmptySearchResultsProps> = ({
  hasActiveSearch,
  searchDescription,
  entityName,
  onClearSearch,
  createButton,
  suggestions,
}) => {
  const defaultSearchSuggestions = suggestions || [
    'Using different keywords',
    'Checking your spelling',
    'Using more general search terms',
    'Clearing or adjusting filters',
  ];

  const createButtonVisible = createButton?.visible !== false;

  return (
    <div className="text-center my-5">
      <div className="alert alert-info d-inline-block">
        {hasActiveSearch ? (
          <>
            <h4>No Results Found</h4>
            {searchDescription && (
              <p className="mb-3">
                No {entityName.toLowerCase()} match <strong>&quot;{searchDescription}&quot;</strong>
              </p>
            )}
            <p className="mb-3">Try:</p>
            <ul className="text-start" style={{ display: 'inline-block' }}>
              {defaultSearchSuggestions.map((suggestion, index) => (
                <li key={index}>{suggestion}</li>
              ))}
            </ul>
            <div className="mt-3">
              {onClearSearch && (
                <Button color="secondary" onClick={onClearSearch} className="me-2">
                  <FontAwesomeIcon icon="times" />
                  &nbsp; Clear Search
                </Button>
              )}
              {createButton && createButtonVisible && (
                <Link to={createButton.to} className="btn btn-primary">
                  <FontAwesomeIcon icon="plus" />
                  &nbsp; {createButton.label}
                </Link>
              )}
            </div>
          </>
        ) : (
          <>
            <h4>No {entityName} Found</h4>
            <p className="mb-3">There are no {entityName.toLowerCase()} in the system yet. Get started by creating your first one.</p>
            {createButton && createButtonVisible && (
              <Link to={createButton.to} className="btn btn-primary btn-lg">
                <FontAwesomeIcon icon="plus" />
                &nbsp; {createButton.label}
              </Link>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default EmptySearchResults;
