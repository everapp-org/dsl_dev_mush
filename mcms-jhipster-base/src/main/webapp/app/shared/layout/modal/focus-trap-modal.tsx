/**
 * Feature #272: Modal focus trap
 *
 * This component wraps reactstrap Modal with focus trap functionality to ensure
 * keyboard users cannot tab out of the modal to background content.
 *
 * Focus trap ensures:
 * - Tab cycles through modal elements only
 * - Shift+Tab cycles in reverse within modal
 * - Background content is not accessible via keyboard
 * - Focus returns to trigger element when modal closes
 */

import React, { ReactNode } from 'react';
import { Modal, ModalProps } from 'reactstrap';
import FocusTrap from 'focus-trap-react';

interface FocusTrapModalProps extends ModalProps {
  children: ReactNode;
  isOpen: boolean;
}

/**
 * Modal with focus trap that prevents keyboard navigation from escaping the modal.
 *
 * Usage:
 * ```tsx
 * <FocusTrapModal isOpen={isOpen} toggle={handleClose}>
 *   <ModalHeader toggle={handleClose}>Title</ModalHeader>
 *   <ModalBody>Content</ModalBody>
 *   <ModalFooter>Actions</ModalFooter>
 * </FocusTrapModal>
 * ```
 */
export const FocusTrapModal: React.FC<FocusTrapModalProps> = ({ isOpen, children, ...modalProps }) => {
  return (
    <Modal isOpen={isOpen} {...modalProps}>
      {isOpen && (
        <FocusTrap
          focusTrapOptions={{
            // Allow clicking outside to close (if toggle is provided)
            clickOutsideDeactivates: true,
            // Return focus to trigger element when modal closes
            returnFocusOnDeactivate: true,
            // Prevent Esc key from closing (let Modal handle it)
            escapeDeactivates: false,
            // Initial focus on first focusable element
            initialFocus: false,
            // Allow focus to wrap around (tab from last to first element)
            fallbackFocus: () => document.querySelector('.modal-content') || document.body,
          }}
        >
          <div>{children}</div>
        </FocusTrap>
      )}
    </Modal>
  );
};

export default FocusTrapModal;
