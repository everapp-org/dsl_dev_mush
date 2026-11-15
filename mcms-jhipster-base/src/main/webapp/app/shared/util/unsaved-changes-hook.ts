import { useEffect, useCallback, useState } from 'react';
import { useBlocker } from 'react-router-dom';

/**
 * Custom hook to warn users about unsaved changes before navigating away
 * @param isDirty - Boolean indicating if the form has unsaved changes
 * @param message - Optional custom message to display (default: "You have unsaved changes. Are you sure you want to leave?")
 * @returns Object containing blocker state and navigation control functions
 */
export const useUnsavedChangesWarning = (
  isDirty: boolean,
  message: string = 'You have unsaved changes. Are you sure you want to leave?',
) => {
  const [showPrompt, setShowPrompt] = useState(false);

  // Block in-app navigation (React Router navigation)
  const blocker = useBlocker(
    useCallback(
      ({ currentLocation, nextLocation }) => {
        // Only block if form is dirty and navigating to a different location
        return isDirty && currentLocation.pathname !== nextLocation.pathname;
      },
      [isDirty],
    ),
  );

  // Block browser navigation (back button, closing tab, etc.)
  useEffect(() => {
    const handleBeforeUnload = (e: BeforeUnloadEvent) => {
      if (isDirty) {
        e.preventDefault();
        // Modern browsers require this for the confirmation dialog to appear
        e.returnValue = message;
        return message;
      }
    };

    window.addEventListener('beforeunload', handleBeforeUnload);

    return () => {
      window.removeEventListener('beforeunload', handleBeforeUnload);
    };
  }, [isDirty, message]);

  // Update showPrompt state when blocker state changes
  useEffect(() => {
    if (blocker.state === 'blocked') {
      setShowPrompt(true);
    } else {
      setShowPrompt(false);
    }
  }, [blocker.state]);

  const confirmNavigation = useCallback(() => {
    if (blocker.state === 'blocked') {
      blocker.proceed();
    }
    setShowPrompt(false);
  }, [blocker]);

  const cancelNavigation = useCallback(() => {
    if (blocker.state === 'blocked') {
      blocker.reset();
    }
    setShowPrompt(false);
  }, [blocker]);

  return {
    showPrompt,
    confirmNavigation,
    cancelNavigation,
    message,
  };
};
