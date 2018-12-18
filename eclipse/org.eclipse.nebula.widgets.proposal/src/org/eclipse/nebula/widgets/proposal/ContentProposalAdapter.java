/*******************************************************************************
 * Copyright (c) 2018, 2019 vogella GmbH and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Simon Scholz <simon.scholz@vogella.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.proposal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.nebula.widgets.proposal.controladapter.IControlAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import reactor.core.publisher.Mono;

/**
 * This class connects the widget, which is wrapped in a {@link IControlAdapter}
 * ,with the {@link ProposalDialog} in order to make proposals for the
 * ControlAdapters widget.
 * 
 * @author Simon Scholz
 *
 */
public class ContentProposalAdapter<T> {

	private List<IProposalChangedListener> proposalSelectedListeners = new ArrayList<IProposalChangedListener>();

	private boolean isEnabled = true;

	private int filterStartCharAmount = 2;

	private ProposalDialog<T> proposalDialog;

	/*
	 * The delay in milliseconds used when autoactivating the popup.
	 */
	private int autoActivationDelay = 0;

	/*
	 * A boolean that indicates whether key events received while the proposal popup
	 * is open should also be propagated to the control. Default value is true.
	 */
	private boolean propagateKeys = true;

	/*
	 * A flag that indicates that we are watching modify events
	 */
	private boolean watchModify = false;

	/*
	 * A boolean indicating whether a keystroke has been received. Used to see if an
	 * autoactivation delay was interrupted by a keystroke.
	 */
	private boolean receivedKeyDown;

	private KeyStroke triggerKeyStroke;

	private String autoActivateString;

	private char[] autoActivationCharacters;

	private IControlAdapter controlAdapter;

	private IProposalConfigurator<T> proposalConfigurator;

	/**
	 * Creates a {@link ContentProposalAdapter}, which manages the connection of a
	 * widget and the {@link ProposalDialog}.
	 * 
	 * @param controlAdapter       is used to access the widget, where the proposals
	 *                             should be shown.
	 * @param proposalConfigurator {@link IProposalConfigurator} is used to
	 *                             configure the {@link ProposalDialog}
	 */
	public ContentProposalAdapter(IControlAdapter controlAdapter, IProposalConfigurator<T> proposalConfigurator) {
		this(controlAdapter, proposalConfigurator, null, null);
	}

	/**
	 * Creates a {@link ContentProposalAdapter}, which manages the connection of a
	 * widget and the {@link ProposalDialog}.
	 * 
	 * @param controlAdapter           is used to access the widget, where the
	 *                                 proposals should be shown.
	 * @param proposalConfigurator     {@link IProposalConfigurator} is used to
	 *                                 configure the {@link ProposalDialog}
	 * @param keyStroke                {@link KeyStroke}, which can be used to open
	 *                                 the {@link ProposalDialog}.
	 * @param autoActivationCharacters can also be used to open the
	 *                                 {@link ProposalDialog}.
	 */
	public ContentProposalAdapter(IControlAdapter controlAdapter, IProposalConfigurator<T> proposalConfigurator,
			KeyStroke keyStroke, char[] autoActivationCharacters) {

		this.controlAdapter = controlAdapter;
		this.proposalConfigurator = proposalConfigurator;
		this.triggerKeyStroke = keyStroke;
		if (autoActivationCharacters != null) {
			this.autoActivateString = new String(autoActivationCharacters);
			this.autoActivationCharacters = autoActivationCharacters;
		}
		addListener(controlAdapter.getControl());
	}

	private void addListener(Control target) {
		Listener controlListener = new Listener() {

			@Override
			public void handleEvent(Event e) {
				if (!isEnabled()) {
					return;
				}

				switch (e.type) {
				case SWT.Traverse:
				case SWT.KeyDown:
					// If the popup is open, it gets first shot at the
					// keystroke and should set the doit flags appropriately.
					if (proposalDialog != null) {
						if (!isValid()) {
							return;
						}

						char key = e.character;

						// Traverse events are handled depending on whether the
						// event has a character.
						if (e.type == SWT.Traverse) {
							// If the traverse event contains a legitimate
							// character,
							// then we must set doit false so that the widget
							// will
							// receive the key event. We return immediately so
							// that
							// the character is handled only in the key event.
							// See
							// https://bugs.eclipse.org/bugs/show_bug.cgi?id=132101
							if (key != 0) {
								e.doit = false;
								return;
							}
							// Traversal does not contain a character. Set doit
							// true
							// to indicate TRAVERSE_NONE will occur and that no
							// key
							// event will be triggered. We will check for
							// navigation
							// keys below.
							e.detail = SWT.TRAVERSE_NONE;
							e.doit = true;
						} else {
							// Default is to only propagate when configured that
							// way.
							// Some keys will always set doit to false anyway.
							e.doit = propagateKeys;
						}

						// No character. Check for navigation keys.

						if (key == 0) {
							Table proposalTable = proposalDialog.getViewer().getTable();
							int newSelection = proposalTable.getSelectionIndex();
							int visibleRows = (proposalTable.getSize().y / proposalTable.getItemHeight()) - 1;
							switch (e.keyCode) {
							case SWT.ARROW_UP:
								newSelection -= 1;
								if (newSelection < 0) {
									newSelection = proposalTable.getItemCount() - 1;
								}
								// Not typical - usually we get this as a
								// Traverse and
								// therefore it never propagates. Added for
								// consistency.
								if (e.type == SWT.KeyDown) {
									// don't propagate to control
									e.doit = false;
								}

								break;

							case SWT.ARROW_DOWN:
								newSelection += 1;
								if (newSelection > proposalTable.getItemCount() - 1) {
									newSelection = 0;
								}
								// Not typical - usually we get this as a
								// Traverse and
								// therefore it never propagates. Added for
								// consistency.
								if (e.type == SWT.KeyDown) {
									// don't propagate to control
									e.doit = false;
								}

								break;

							case SWT.PAGE_DOWN:
								newSelection += visibleRows;
								if (newSelection >= proposalTable.getItemCount()) {
									newSelection = proposalTable.getItemCount() - 1;
								}
								if (e.type == SWT.KeyDown) {
									// don't propagate to control
									e.doit = false;
								}
								break;

							case SWT.PAGE_UP:
								newSelection -= visibleRows;
								if (newSelection < 0) {
									newSelection = 0;
								}
								if (e.type == SWT.KeyDown) {
									// don't propagate to control
									e.doit = false;
								}
								break;

							case SWT.HOME:
								newSelection = 0;
								if (e.type == SWT.KeyDown) {
									// don't propagate to control
									e.doit = false;
								}
								break;

							case SWT.END:
								newSelection = proposalTable.getItemCount() - 1;
								if (e.type == SWT.KeyDown) {
									// don't propagate to control
									e.doit = false;
								}
								break;

							// If received as a Traverse, these should propagate
							// to the control as keydown. If received as a
							// keydown,
							// proposals should be recomputed since the cursor
							// position has changed.
							case SWT.ARROW_LEFT:
							case SWT.ARROW_RIGHT:
								if (e.type == SWT.Traverse) {
									e.doit = false;
									// TODO } else {
									// e.doit = true;
									// String contents = controlAdapter
									// .getContent();
									// // If there are no contents, changes in
									// // cursor
									// // position have no effect. Note also
									// that
									// // we do
									// // not affect the filter text on
									// ARROW_LEFT
									// // as
									// // we would with BS.
									// if (contents.length() > 0) {
									// asyncRecomputeProposals(filterText);
									// }
								}
								break;

							// Any unknown keycodes will cause the popup to
							// close.
							// Modifier keys are explicitly checked and ignored
							// because
							// they are not complete yet (no character).
							default:
								if (e.keyCode != SWT.CAPS_LOCK && e.keyCode != SWT.NUM_LOCK && e.keyCode != SWT.MOD1
										&& e.keyCode != SWT.MOD2 && e.keyCode != SWT.MOD3 && e.keyCode != SWT.MOD4) {
									closeProposalPopup();
								}
								return;
							}

							// If any of these navigation events caused a new
							// selection,
							// then handle that now and return.
							if (newSelection >= 0) {
								proposalTable.setSelection(newSelection);
								proposalTable.showSelection();
							}
							return;
						}

						// key != 0
						// Check for special keys involved in cancelling,
						// accepting, or
						// filtering the proposals.
						switch (key) {
						case SWT.ESC:
							e.doit = false;
							closeProposalPopup();
							break;

						case SWT.LF:
						case SWT.CR:
							e.doit = false;
							watchModify = false;
							ISelection selection = proposalDialog.getViewer().getSelection();
							if (selection != null) {
								closeProposalPopup();
								fireSelectionChanged(selection);
								controlAdapter.getControl().setFocus();
							} else {
								closeProposalPopup();
							}
							break;

						case SWT.TAB:
							e.doit = false;
							proposalDialog.getShell().setFocus();
							return;

						case SWT.BS:
							String content = controlAdapter.getContent();
							if (content.length() > 1) {
								reloadProposals(content.substring(0, content.length() - 1));
							}
							break;

						default:
							String contentDefault = controlAdapter.getContent();
							reloadProposals(contentDefault + e.character);
							break;
						}
						// See
						// https://bugs.eclipse.org/bugs/show_bug.cgi?id=192633
						// If the popup is open and this is a valid character,
						// we
						// want to watch for the modified text.
						if (isPropagateKeys() && e.character != 0)
							watchModify = true;

						return;
					}

					// We were only listening to traverse events for the popup
					if (e.type == SWT.Traverse) {
						return;
					}

					// The popup is not open. We are looking at keydown events
					// for a trigger to open the popup.
					if (triggerKeyStroke != null) {
						// Either there are no modifiers for the trigger and we
						// check the character field...
						if ((triggerKeyStroke.getModifierKeys() == KeyStroke.NO_KEY
								&& triggerKeyStroke.getNaturalKey() == e.character) ||
						// ...or there are modifiers, in which case the
						// keycode and state must match
						(triggerKeyStroke.getNaturalKey() == e.keyCode
								&& ((triggerKeyStroke.getModifierKeys() & e.stateMask) == triggerKeyStroke
										.getModifierKeys()))) {
							// We never propagate the keystroke for an explicit
							// keystroke invocation of the popup
							e.doit = false;
							openProposalPopup();
							return;
						}
					}
					/*
					 * The triggering keystroke was not invoked. If a character was typed, compare
					 * it to the autoactivation characters.
					 */
					if (e.character != 0) {
						if (autoActivateString != null) {
							if (autoActivateString.indexOf(e.character) >= 0) {
								autoActivate();
							} else {
								// No autoactivation occurred, so record the key
								// down as a means to interrupt any
								// autoactivation that is pending due to
								// autoactivation delay.
								receivedKeyDown = true;
								// watch the modify so we can close the popup in
								// cases where there is no longer a trigger
								// character in the content
								watchModify = true;
							}
						} else {
							// The autoactivate string is null. If the trigger
							// is also null, we want to act on any modification
							// to the content. Set a flag so we'll catch this
							// in the modify event.
							if (triggerKeyStroke == null) {
								watchModify = true;
							}
						}
					} else {
						// A non-character key has been pressed. Interrupt any
						// autoactivation that is pending due to autoactivation
						// delay.
						receivedKeyDown = true;
					}
					break;

				// There are times when we want to monitor content changes
				// rather than individual keystrokes to determine whether
				// the popup should be closed or opened based on the entire
				// content of the control.
				// The watchModify flag ensures that we don't autoactivate if
				// the content change was caused by something other than typing.
				// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=183650
				case SWT.Modify:
					if (allowsAutoActivate() && watchModify) {
						watchModify = false;
						// We are in autoactivation mode, either for specific
						// characters or for all characters. In either case,
						// we should close the proposal popup when there is no
						// content in the control.
						if (controlAdapter.isEmpty()) {
							// see
							// https://bugs.eclipse.org/bugs/show_bug.cgi?id=192633
							closeProposalPopup();
						} else {
							// See
							// https://bugs.eclipse.org/bugs/show_bug.cgi?id=147377
							// Given that we will close the popup when there are
							// no valid proposals, we must consider reopening it
							// on any
							// content change when there are no particular
							// autoActivation
							// characters
							if (autoActivateString == null) {
								autoActivate();
							} else {
								// Autoactivation characters are defined, but
								// this
								// modify event does not involve one of them.
								// See
								// if any of the autoactivation characters are
								// left
								// in the content and close the popup if none
								// remain.
								if (!shouldPopupRemainOpen())
									closeProposalPopup();
							}
						}
					}
					break;
				default:
					break;
				}
			}

		};
		target.addListener(SWT.KeyDown, controlListener);
		target.addListener(SWT.Traverse, controlListener);
		target.addListener(SWT.Modify, controlListener);

		ControlListener popUpCloseListener = new ControlListener() {

			@Override
			public void controlResized(ControlEvent e) {
				closeProposalPopup();

			}

			@Override
			public void controlMoved(ControlEvent e) {
				closeProposalPopup();

			}
		};

		controlAdapter.getControl().addControlListener(popUpCloseListener);

		controlAdapter.getControl().getShell().addControlListener(popUpCloseListener);
	}

	/**
	 * Check whether proposals are activated.
	 * 
	 * @return <code>true</code> if enabled and <code>false</code> otherwise
	 */
	public boolean isEnabled() {
		return isEnabled;
	}

	/**
	 * Enable or disable the proposals.
	 * 
	 * @param isEnabled <code>true</code> if proposals should be enabled and
	 *                  <code>false</code> if not.
	 */
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	/**
	 * Used to determine if the typed keys should be propagated to the target widget
	 * or not.
	 * 
	 * @return <code>true</code> if keys are propagated and <code>false</code>
	 *         otherwise
	 */
	public boolean isPropagateKeys() {
		return propagateKeys;
	}

	/**
	 * Define whether key should be propagated to the target widget or not.
	 * 
	 * @param propagateKeys <code>true</code> if key should be propagated to the
	 *                      target widget and <code>false</code> otherwise
	 */
	public void setPropagateKeys(boolean propagateKeys) {
		this.propagateKeys = propagateKeys;
	}

	/**
	 * Closes the {@link ProposalDialog}, if it is open.
	 */
	protected void closeProposalPopup() {
		if (proposalDialog != null) {
			proposalDialog.close();
		}
	}

	/**
	 * Return whether a proposal popup should remain open. If it was autoactivated
	 * by specific characters, and none of those characters remain, then it should
	 * not remain open. This method should not be used to determine whether
	 * autoactivation has occurred or should occur, only whether the circumstances
	 * would dictate that a popup remain open.
	 */
	private boolean shouldPopupRemainOpen() {
		// If we always autoactivate or never autoactivate, it should remain
		// open
		if (autoActivateString == null || autoActivateString.length() == 0)
			return true;
		String content = controlAdapter.getContent();
		for (int i = 0; i < autoActivateString.length(); i++) {
			if (content.indexOf(autoActivateString.charAt(i)) >= 0)
				return true;
		}
		return false;
	}

	/**
	 * Return whether this adapter is configured for autoactivation, by specific
	 * characters or by any characters.
	 */
	private boolean allowsAutoActivate() {
		return (autoActivateString != null && autoActivateString.length() > 0)
				|| (autoActivateString == null && triggerKeyStroke == null);
	}

	/**
	 * Autoactivation has been triggered. Open the popup using any specified delay.
	 */
	private void autoActivate() {
		if (autoActivationDelay > 0) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					receivedKeyDown = false;
					try {
						Thread.sleep(autoActivationDelay);
					} catch (InterruptedException e) {
					}
					if (!isValid() || receivedKeyDown) {
						return;
					}
					controlAdapter.getControl().getDisplay().syncExec(new Runnable() {
						@Override
						public void run() {
							openProposalPopup();
						}
					});
				}
			};
			Thread t = new Thread(runnable);
			t.start();
		} else {
			// Since we do not sleep, we must open the popup
			// in an async exec. This is necessary because
			// this method may be called in the middle of handling
			// some event that will cause the cursor position or
			// other important info to change as a result of this
			// event occurring.
			controlAdapter.getControl().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (isValid()) {
						openProposalPopup();
					}
				}
			});
		}
	}

	/**
	 * Check that the control and content adapter are valid.
	 */
	private boolean isValid() {
		return controlAdapter != null && controlAdapter.getControl() != null
				&& !controlAdapter.getControl().isDisposed();
	}

	/**
	 * Open a new {@link ProposalDialog} and initialize it.
	 */
	public void openProposalPopup() {
		if (isValid()) {
			if (proposalDialog == null) {
				// Check whether there are any proposals to be shown.
				proposalDialog = new ProposalDialog<T>(controlAdapter, proposalConfigurator);
				proposalDialog.open();
				reloadProposals(controlAdapter.getContent());
				proposalDialog.getShell().addDisposeListener(new DisposeListener() {
					@Override
					public void widgetDisposed(DisposeEvent event) {
						proposalDialog = null;
					}
				});
				proposalDialog.getViewer().addDoubleClickListener(new IDoubleClickListener() {

					@Override
					public void doubleClick(DoubleClickEvent event) {
						closeProposalPopup();
						fireSelectionChanged(event.getSelection());
						controlAdapter.getControl().setFocus();
					}
				});
				internalPopupOpened();
			}
		}
	}

	/**
	 * The popup has just opened, but listeners have not yet been notified. Perform
	 * any cleanup that is needed.
	 */
	private void internalPopupOpened() {
		if (isValid() && controlAdapter.getControl() instanceof Combo) {
			((Combo) controlAdapter.getControl()).setListVisible(false);
		}
	}

	/**
	 * Add a listener, which will be informed if a proposal was selected.
	 * 
	 * @param listener {@link IProposalChangedListener}
	 */
	public void addSelectionChangedListener(IProposalChangedListener listener) {
		proposalSelectedListeners.add(listener);
	}

	/**
	 * Remove {@link IProposalChangedListener}
	 * 
	 * @param listener {@link IProposalChangedListener}
	 */
	public void removeSelectionChangedListener(IProposalChangedListener listener) {
		proposalSelectedListeners.remove(listener);
	}

	public void reloadProposals(String filterContent) {
		if (isValid()) {

			if (autoActivationCharacters != null) {
				int caretOffset = controlAdapter.getCaretOffset();
				filterContent = filterContent.substring(0, caretOffset);

				int lastIndexOf = -1;
				for (char c : autoActivationCharacters) {
					lastIndexOf = Math.max(filterContent.lastIndexOf(c), lastIndexOf);
				}

				if (lastIndexOf != -1) {
					filterContent = filterContent.substring(lastIndexOf);
				}
			}
			if (filterContent != null) {
				if (filterContent.length() < getFilterStartCharAmount()) {
					// avoid filtering by passing null to the getInput method
					filterContent = null;
				}
			}

			Mono<T> input = proposalConfigurator.getInput(filterContent);
			input.doOnSubscribe(s -> proposalDialog.showLoading()).subscribe(proposalDialog::setInput,
					this::handleError);
		}
	}

	private void handleError(Throwable err) {
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		Status status = new Status(IStatus.ERROR, bundle.getSymbolicName(), err.getMessage(), err);
		ErrorDialog.openError(controlAdapter.getControl().getShell(), "Error", err.getMessage(), status);
	}

	/**
	 * Get the amount of characters, which have to be typed until filtering starts
	 * 
	 * @return amount of characters
	 */
	public int getFilterStartCharAmount() {
		return filterStartCharAmount;
	}

	/**
	 * Set the amount of characters, which have to be typed until filtering starts
	 * 
	 * @param filterStartCharAmount - amount of characters
	 */
	public void setFilterStartCharAmount(int filterStartCharAmount) {
		this.filterStartCharAmount = filterStartCharAmount;
	}

	/**
	 * Informs the attached {@link IProposalChangedListener}, that a certain
	 * proposal was selected.
	 * 
	 * @param selection {@link ISelection}
	 */
	protected void fireSelectionChanged(ISelection selection) {
		for (IProposalChangedListener listener : proposalSelectedListeners) {
			listener.proposalChanged(selection);
		}
	}
}
