import React from 'react';
import { Spinner } from 'reactstrap';

export interface LoadingSpinnerProps {
  /**
   * Size variant:
   * - 'large': 3rem (for full page/section loading)
   * - 'medium': 2rem (for inline loading, "load more" scenarios)
   * - 'small': 1rem (for button/compact scenarios)
   */
  size?: 'large' | 'medium' | 'small';
  /**
   * Message displayed alongside the spinner
   */
  message?: string;
  /**
   * Center the spinner vertically and horizontally
   */
  centered?: boolean;
  /**
   * Minimum height for centered container
   */
  minHeight?: string;
  /**
   * Custom className for the wrapper
   */
  className?: string;
}

const sizeMap = {
  large: { width: '3rem', height: '3rem' },
  medium: { width: '2rem', height: '2rem' },
  small: { width: '1rem', height: '1rem' },
};

/**
 * Consistent loading spinner component used across the application
 *
 * @example
 * // Full page loading
 * <LoadingSpinner size="large" message="Loading batch list..." centered minHeight="200px" />
 *
 * @example
 * // Inline loading
 * <LoadingSpinner size="medium" message="Loading more..." />
 *
 * @example
 * // Compact loading
 * <LoadingSpinner size="small" />
 */
export const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({
  size = 'large',
  message,
  centered = false,
  minHeight = '200px',
  className = '',
}) => {
  const spinnerStyle = sizeMap[size];

  const spinner = (
    <>
      <Spinner color="primary" style={spinnerStyle}>
        {message || 'Loading...'}
      </Spinner>
      {message && <span className="ms-2">{message}</span>}
    </>
  );

  if (centered) {
    return (
      <div className={`d-flex justify-content-center align-items-center ${className}`} style={{ minHeight }}>
        {spinner}
      </div>
    );
  }

  return <div className={`d-flex align-items-center ${className}`}>{spinner}</div>;
};

export default LoadingSpinner;
