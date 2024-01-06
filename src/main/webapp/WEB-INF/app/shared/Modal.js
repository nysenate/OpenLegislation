import React from 'react'
import {
  DialogContent,
  DialogOverlay
} from "@reach/dialog";
import "@reach/dialog/styles.css";


/**
 * Displays a dialog/modal window. Uses the ReachUI Dialog library https://reach.tech/dialog/.
 * @param isOpen {boolean} if true the modal is displayed, if false it is hidden.
 * @param onDismiss {callback} This callback is executed when the user presses escape or clicks outside the modal.
 *                             Typically, this should close the modal, but it doesn't have to.
 * @param ariaLabel {string} A label which identifies the purpose of this modal. Used by screen readers.
 * @param children {JSX.Element} This will be rendered inside the modal.
 */
export default function Modal({ isOpen, onDismiss, ariaLabel, children, className }) {
  return (
    <DialogOverlay isOpen={isOpen} onDismiss={onDismiss} className={`z-20 ${className}`}>
      <DialogContent className="rounded" aria-label={ariaLabel}>
        {children}
      </DialogContent>
    </DialogOverlay>
  )
}
