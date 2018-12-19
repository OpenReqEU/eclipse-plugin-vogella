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

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.nebula.widgets.proposal.controladapter.IControlAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * <p>
 * This class is used to show proposals, which can be selected by a user. This
 * {@link PopupDialog} will be rendered underneath the targetControl, which is
 * passes to the constructor. The {@link PopupDialog} itself contains a
 * {@link TableViewer} with certain elements, which can be selected.
 * </p>
 * <p>
 * Until the input of the {@link TableViewer} is delivered "Loading..." will be
 * displayed on the {@link PopupDialog}.
 * </p>
 * 
 * @author Simon Scholz
 *
 */
public class ProposalDialog<T> extends PopupDialog {

	/*
	 * The character height hint for the popup. May be overridden by using
	 * setInitialPopupSize.
	 */
	private static final int POPUP_CHAR_HEIGHT = 10;

	/*
	 * The minimum pixel width for the popup. May be overridden by using
	 * setInitialPopupSize.
	 */
	private static final int POPUP_MINIMUM_WIDTH = 300;

	private Point popupSize;

	private TableViewer tableViewer;
	
	private WritableList<T> elements = new WritableList<>();

	private Composite stackParent;

	private IProposalConfigurator<T> proposalConfigurator;

	private Control loadingControl;

	private IControlAdapter target;

	/**
	 * Creates a {@link ProposalDialog} with one table column and uses the a
	 * {@link ColumnLabelProvider} to show the input.
	 * 
	 * @param target {@link Control} where the {@link ProposalDialog} should be
	 *               rendered.
	 * @param input  of the {@link TableViewer}, which is shown on the
	 *               {@link ProposalDialog}
	 */
	public ProposalDialog(IControlAdapter target, T input) {
		this(target, input, new ColumnLabelProvider());
	}

	/**
	 * Creates a {@link ProposalDialog} with one table column and uses the given
	 * {@link CellLabelProvider} to show the input.
	 * 
	 * @param target        {@link Control} where the {@link ProposalDialog} should
	 *                      be rendered.
	 * @param labelProvider {@link CellLabelProvider} which will be used to show the
	 *                      given input
	 * @param input         of the {@link TableViewer}, which is shown on the
	 *                      {@link ProposalDialog}
	 */
	public ProposalDialog(IControlAdapter target, T input, CellLabelProvider labelProvider) {
		this(target, new DirectInputProposalConfigurator<T>(input, labelProvider));
	}

	/**
	 * 
	 * Creates a {@link ProposalDialog}, which is configured by an
	 * {@link IProposalConfigurator}.
	 * 
	 * @param target               {@link Control} where the {@link ProposalDialog}
	 *                             should be rendered.
	 * @param proposalConfigurator {@link IProposalConfigurator}, where the
	 *                             {@link TableViewer} of the proposal dialog and
	 *                             it's input can be configured.
	 */
	public ProposalDialog(IControlAdapter target, IProposalConfigurator<T> proposalConfigurator) {
		super(target.getControl().getShell(), SWT.RESIZE | SWT.ON_TOP | SWT.TOOL | SWT.NO_TRIM, false, true, false,
				false, false, null, null);
		this.target = target;
		this.proposalConfigurator = proposalConfigurator;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite comp = (Composite) super.createDialogArea(parent);

		stackParent = new Composite(comp, SWT.NONE);
		StackLayout stackLayout = new StackLayout();
		stackParent.setLayout(stackLayout);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(stackParent);

		loadingControl = createLoadingInfo();
		// only show loading control on the stackparent until table gets its
		// input see setInput method.
		stackLayout.topControl = loadingControl;

		tableViewer = new TableViewer(stackParent, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		tableViewer.getTable().setHeaderVisible(false);
		tableViewer.getTable().setLinesVisible(false);
		tableViewer.getTable().addListener(SWT.EraseItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Control control = (Control) event.widget;
				event.gc.setBackground(control.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				event.gc.fillRectangle(event.getBounds());
			}
		});

		tableViewer.setContentProvider(new ObservableListContentProvider());
		
		tableViewer.setInput(elements);

		proposalConfigurator.configureViewer(tableViewer);

		return comp;
	}

	protected Control createLoadingInfo() {
		Composite loadingComposite = new Composite(stackParent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(loadingComposite);
		// TODO LoadingCanvas loadingCanvas = new
		// LoadingCanvas(loadingComposite);
		// GridDataFactory.swtDefaults().applyTo(loadingCanvas);
		Label label = new Label(loadingComposite, SWT.FLAT);
		label.setText("Loading...");
		GridDataFactory.swtDefaults().applyTo(label);
		return loadingComposite;
	}

	/**
	 * Add a new element to the {@link TableViewer}s input and places the
	 * {@link TableViewer} on top of the {@link StackLayout}, if it is not already
	 * on top.
	 * 
	 * @param element of the {@link TableViewer}
	 */
	public void addElement(T element) {
		elements.add(element);
		tableViewer.refresh();
		setStackTopControl(tableViewer.getControl());
	}

	/**
	 * Remove an element to the {@link TableViewer}s input and places the
	 * {@link TableViewer} on top of the {@link StackLayout}, if it is not already
	 * on top.
	 * 
	 * @param element of the {@link TableViewer}
	 */
	public void removeElement(T element) {
		elements.remove(element);
		tableViewer.refresh();
		setStackTopControl(tableViewer.getControl());
	}

	/**
	 * Add a new element to the {@link TableViewer}s input and places the
	 * {@link TableViewer} on top of the {@link StackLayout}, if it is not already
	 * on top.
	 * 
	 * @param element of the {@link TableViewer}
	 */
	public void clearElements() {
		elements.clear();
		tableViewer.refresh();
		setStackTopControl(tableViewer.getControl());
	}

	public void showLoading() {
		setStackTopControl(loadingControl);
	}

	public void showError(Exception exception) {
		// TODO show error control
	}

	protected void setStackTopControl(Control topControl) {
		Layout layout = stackParent.getLayout();
		if (layout instanceof StackLayout) {
			StackLayout stackLayout = (StackLayout) layout;
			if (!(stackLayout.topControl.equals(topControl))) {
				((StackLayout) layout).topControl = topControl;
				stackParent.layout();
			}
		}
	}

	@Override
	protected void adjustBounds() {
		// Get our control's location in display coordinates.
		Point location = target.getControl().getDisplay().map(target.getControl().getParent(), null,
				target.getControl().getLocation());
		int initialX = location.x;
		int initialY = location.y + target.getControl().getSize().y;
		// If we are inserting content, use the cursor position to
		// position the control.
		Rectangle insertionBounds = target.getInsertionBounds();
		initialX = initialX + insertionBounds.x;
		initialY = location.y + insertionBounds.y + insertionBounds.height;

		// If there is no specified size, force it by setting
		// up a layout on the table.
		if (popupSize == null) {
			GridData data = new GridData(GridData.FILL_BOTH);
			data.heightHint = tableViewer.getTable().getItemHeight() * POPUP_CHAR_HEIGHT;
			data.widthHint = Math.max(target.getControl().getSize().x, POPUP_MINIMUM_WIDTH);
			tableViewer.getControl().setLayoutData(data);
			getShell().pack();
			popupSize = getShell().getSize();
			popupSize.y = data.heightHint;
		}

		// Constrain to the display
		Rectangle constrainedBounds = getConstrainedShellBounds(
				new Rectangle(initialX, initialY, popupSize.x, popupSize.y));

		// If there has been an adjustment causing the popup to overlap
		// with the control, then put the popup above the control.
		if (constrainedBounds.y < initialY)
			getShell().setBounds(initialX, location.y - popupSize.y, popupSize.x, popupSize.y);
		else
			getShell().setBounds(initialX, initialY, popupSize.x, popupSize.y);

		// Now set up a listener to monitor any changes in size.
		getShell().addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event e) {
				popupSize = getShell().getSize();
			}
		});
	}

	@Override
	protected Color getBackground() {
		return Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	}

	/**
	 * Get the {@link Rectangle}, where the {@link PopupDialog} whould be rendered.
	 * 
	 * @param control target
	 * @return {@link Rectangle}
	 */
	public Rectangle getInsertionBounds(Control control) {
		Text text = (Text) control;
		Point caretOrigin = text.getCaretLocation();
		// We fudge the y pixels due to problems with getCaretLocation
		// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=52520
		return new Rectangle(caretOrigin.x + text.getClientArea().x, caretOrigin.y + text.getClientArea().y + 3, 1,
				text.getLineHeight());
	}

	/**
	 * Get the {@link TableViewer}, which is shown on this propsal dialog.
	 * 
	 * @return {@link TableViewer}
	 */
	public TableViewer getViewer() {
		return tableViewer;
	}

}
