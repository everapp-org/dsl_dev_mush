/**
 * Feature #280: Loading States Announced
 *
 * React hook that announces loading states to screen readers via aria-live regions.
 * This ensures users with assistive technologies are informed when async operations
 * are in progress or have completed.
 *
 * WCAG 2.1 Success Criterion 4.1.3 (Status Messages)
 */

import { useEffect, useRef } from 'react';
import { announceToScreenReader } from '../util/screen-reader-announcer';

export interface UseLoadingAnnouncerOptions {
  /**
   * The loading state to monitor
   */
  loading: boolean;

  /**
   * Custom message to announce when loading starts
   * @default "Loading..."
   */
  loadingMessage?: string;

  /**
   * Custom message to announce when loading completes
   * @default "Loaded"
   */
  loadedMessage?: string;

  /**
   * ARIA politeness level for announcements
   * - 'polite': Wait for screen reader to finish current announcement
   * - 'assertive': Interrupt screen reader immediately
   * @default 'polite'
   */
  politeness?: 'polite' | 'assertive';

  /**
   * Whether to announce completion
   * @default true
   */
  announceCompletion?: boolean;

  /**
   * Debounce time in milliseconds to avoid announcing rapid state changes
   * @default 300
   */
  debounceMs?: number;
}

/**
 * Hook that announces loading state changes to screen readers
 *
 * @example
 * ```tsx
 * const loading = useAppSelector(state => state.strain.loading);
 * useLoadingAnnouncer({ loading, loadingMessage: 'Refreshing strain list...' });
 * ```
 *
 * @param options - Configuration options for loading announcements
 */
export const useLoadingAnnouncer = (options: UseLoadingAnnouncerOptions): void => {
  const {
    loading,
    loadingMessage = 'Loading...',
    loadedMessage = 'Loaded',
    politeness = 'polite',
    announceCompletion = true,
    debounceMs = 300,
  } = options;

  const previousLoading = useRef<boolean | null>(null);
  const debounceTimer = useRef<ReturnType<typeof setTimeout> | null>(null);

  useEffect(() => {
    // Clear any pending debounce timers
    if (debounceTimer.current) {
      clearTimeout(debounceTimer.current);
    }

    // Skip if loading state hasn't changed
    if (previousLoading.current === loading) {
      return;
    }

    // Debounce announcements to avoid rapid changes
    debounceTimer.current = setTimeout(() => {
      if (loading && previousLoading.current === false) {
        // Loading started
        announceToScreenReader(loadingMessage, politeness);
      } else if (!loading && previousLoading.current === true && announceCompletion) {
        // Loading completed
        announceToScreenReader(loadedMessage, politeness);
      }

      previousLoading.current = loading;
    }, debounceMs);

    // Cleanup on unmount
    return () => {
      if (debounceTimer.current) {
        clearTimeout(debounceTimer.current);
      }
    };
  }, [loading, loadingMessage, loadedMessage, politeness, announceCompletion, debounceMs]);
};

/**
 * Simplified hook for basic loading announcements with default messages
 *
 * @example
 * ```tsx
 * const loading = useAppSelector(state => state.strain.loading);
 * useSimpleLoadingAnnouncer(loading);
 * ```
 *
 * @param loading - The loading state to monitor
 */
export const useSimpleLoadingAnnouncer = (loading: boolean): void => {
  useLoadingAnnouncer({ loading });
};
