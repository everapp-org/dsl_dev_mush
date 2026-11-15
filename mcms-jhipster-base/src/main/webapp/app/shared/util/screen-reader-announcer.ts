/**
 * Feature #271: Screen Reader Announcer Utility
 *
 * Provides utilities for announcing messages to screen readers via aria-live regions.
 * This ensures form validation errors and other important messages are accessible
 * to users relying on assistive technologies.
 */

/**
 * Announces a message to screen readers by updating the global aria-live region.
 *
 * @param message - The message to announce
 * @param politeness - The aria-live politeness level ('polite' or 'assertive')
 */
export const announceToScreenReader = (message: string, politeness: 'polite' | 'assertive' = 'polite'): void => {
  const announcer = document.getElementById('sr-announcements');

  if (!announcer) {
    console.warn('Screen reader announcer element not found');
    return;
  }

  // Update the politeness level
  announcer.setAttribute('aria-live', politeness);

  // Clear and set new message (forces screen reader to announce)
  announcer.textContent = '';

  // Use setTimeout to ensure the clear happens before the new message
  setTimeout(() => {
    announcer.textContent = message;
  }, 100);
};

/**
 * Announces form validation errors to screen readers.
 *
 * @param errors - Object containing field names and their error messages
 */
export const announceFormErrors = (errors: Record<string, string>): void => {
  const errorCount = Object.keys(errors).length;

  if (errorCount === 0) {
    return;
  }

  const fieldNames = Object.keys(errors).join(', ');
  const message =
    errorCount === 1
      ? `Form validation error: ${errors[Object.keys(errors)[0]]}`
      : `${errorCount} form validation errors. Fields with errors: ${fieldNames}`;

  announceToScreenReader(message, 'assertive');
};

/**
 * Clears the screen reader announcement region.
 */
export const clearScreenReaderAnnouncement = (): void => {
  const announcer = document.getElementById('sr-announcements');
  if (announcer) {
    announcer.textContent = '';
  }
};
