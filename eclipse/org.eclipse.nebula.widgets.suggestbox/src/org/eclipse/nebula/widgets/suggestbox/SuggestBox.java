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
package org.eclipse.nebula.widgets.suggestbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.nebula.widgets.suggestbox.listener.SuggestBoxListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;

/**
 * This widget contains a {@link Text} widget and has the possibility to add
 * {@link SuggestBoxEntry} objects, which are rendered before the {@link Text}
 * widget.
 * 
 * @author Simon Scholz
 *
 * @param <T>
 *            types of the elements, which are selected in this SuggestBox.
 */
public class SuggestBox<T> extends Composite {

	public static final int INFINITE_SUGGESTBOXENTRY_AMOUNT = -1;

	private ResourceManager resourceManager;

	private int maxElements = INFINITE_SUGGESTBOXENTRY_AMOUNT;

	private Composite boxComposite;
	private Text text;

	private List<SuggestBoxEntry<T>> suggestBoxEntries;

	public SuggestBox(Composite parent, int style) {
		super(parent, style | SWT.BORDER);
		resourceManager = new LocalResourceManager(
				JFaceResources.getResources(), this);
		setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		suggestBoxEntries = new ArrayList<SuggestBoxEntry<T>>();
		createWidgets();
		initListener();
	}

	/**
	 * Get the Text control, which is part of the {@link SuggestBox}.
	 * 
	 * @return {@link Text}
	 */
	public Text getTextControl() {
		return text;
	}

	/**
	 * Get all {@link SuggestBoxEntry} objects, which are shown on the widget.
	 * 
	 * @return collection of {@link SuggestBoxEntry}
	 */
	public Collection<SuggestBoxEntry<T>> getSuggestBoxEntries() {
		return suggestBoxEntries;
	}

	/**
	 * Get all elements, which are selected and therefore represented by a
	 * {@link SuggestBoxEntry}.
	 * 
	 * @return collection of elements, which are selected in this widget.
	 */
	public Collection<T> getElements() {
		Collection<T> input = new ArrayList<T>();
		for (SuggestBoxEntry<T> suggestBoxEntry : getSuggestBoxEntries()) {
			input.add(suggestBoxEntry.getInput());
		}
		return input;
	}

	/**
	 * Get the maximum amount of {@link SuggestBoxEntry} objects, which this
	 * {@link SuggestBox} may contain.
	 *
	 * @return maximum amount of {@link SuggestBoxEntry} objects
	 */
	public int getMaxElements() {
		return maxElements;
	}

	/**
	 * <p>
	 * Set the maximum amount of {@link SuggestBoxEntry} objects, which this
	 * {@link SuggestBox} may contain.
	 * </p>
	 * <p>
	 * If the current amount of {@link SuggestBoxEntry} inside this
	 * {@link SuggestBox} is already higher than the max elements, the last
	 * {@link SuggestBoxEntry} objects will be removed.
	 * </p>
	 * <p>
	 * In case the maxElements are reached, when invoking this method the
	 * {@link Text} control will be hidden from the layout, as typing text in
	 * order to get proposals is not allowed then.
	 * </p>
	 * 
	 * @param maxElements
	 *            maximum amount of {@link SuggestBoxEntry} objects or
	 *            {@link SuggestBox#INFINITE_SUGGESTBOXENTRY_AMOUNT}
	 */
	public void setMaxElements(int maxElements) {
		this.maxElements = maxElements;
		if (maxElements != INFINITE_SUGGESTBOXENTRY_AMOUNT
				&& maxElements < getSuggestBoxEntries().size()) {
			for (int i = getSuggestBoxEntries().size() - maxElements - 1; i >= 0; i--) {
				removeBox(i);
			}
		} else if (maxElements == getSuggestBoxEntries().size()) {
			changeVisible(getTextControl(), false);
		} else {
			changeVisible(getTextControl(), true);
		}
	}

	private void initListener() {
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (SWT.BS == e.character) {
					if (!suggestBoxEntries.isEmpty()
							&& text.getCaretPosition() == 0) {
						removeBox(suggestBoxEntries.size() - 1);
					}
				}
			}
		});
	}

	private void createWidgets() {
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(this);
		boxComposite = new Composite(this, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.marginTop = 2;
		rowLayout.marginBottom = 2;
		rowLayout.marginLeft = 2;
		rowLayout.marginRight = 2;
		rowLayout.spacing = LayoutConstants.getSpacing().x;
		boxComposite.setLayout(rowLayout);
		boxComposite.setVisible(false);
		GridDataFactory.fillDefaults().exclude(true).applyTo(boxComposite);
		text = new Text(this, SWT.NONE);
		text.setFont(resourceManager.createFont(getFontDescriptor()));
		text.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(text);
	}

	private FontDescriptor getFontDescriptor() {
		FontData[] copy = FontDescriptor.copy(getFont().getFontData());
		for (FontData fontData : copy) {
			fontData.height += 2;
		}
		return FontDescriptor.createFrom(copy);
	}

	/**
	 * Add a new entry to this widget.
	 * 
	 * @param suggestBoxEntry
	 *            {@link SuggestBoxEntry}
	 */
	public void addBox(SuggestBoxEntry<T> suggestBoxEntry) {
		if (getMaxElements() == INFINITE_SUGGESTBOXENTRY_AMOUNT
				|| getSuggestBoxEntries().size() < getMaxElements()) {
			suggestBoxEntries.add(suggestBoxEntry);
			checkTextVisible();
			suggestBoxEntry.create(boxComposite);
			suggestBoxEntry.addListener(SWT.Dispose, new SuggestBoxListener<T>() {

				@Override
				public void handleEvent(SuggestBoxEntry<T> suggestBoxEntry,
						Event event) {
					suggestBoxEntries.remove(suggestBoxEntry);
					if (suggestBoxEntries.isEmpty()) {
						changeVisible(boxComposite, false);
					}
					SuggestBox.this.layout();
				}
			});
			changeVisible(boxComposite, true);
			boxComposite.pack();
		}
	}

	/**
	 * remove an entry from this widget.
	 * 
	 * @param suggestBoxEntry
	 *            {@link SuggestBoxEntry}
	 */
	public void removeBox(SuggestBoxEntry<T> suggestBoxEntry) {
		suggestBoxEntry.dispose();
		checkTextVisible();
		SuggestBox.this.layout();
	}

	/**
	 * Remove an entry at a given index.
	 * 
	 * @param index
	 *            of the {@link SuggestBoxEntry}
	 */
	public void removeBox(int index) {
		SuggestBoxEntry<?> remove = suggestBoxEntries.get(index);
		remove.dispose();
		checkTextVisible();
		SuggestBox.this.layout();
	}

	private void checkTextVisible() {
		if (getSuggestBoxEntries().size() == getMaxElements()) {
			changeVisible(getTextControl(), false);
		} else {
			changeVisible(getTextControl(), true);
		}
	}

	private void changeVisible(Control control, boolean visible) {
		if (control.isVisible() != visible) {
			Object controlslayoutData = control.getLayoutData();
			control.setVisible(visible);
			if (controlslayoutData instanceof RowData) {
				((RowData) controlslayoutData).exclude = !visible;
			} else if (controlslayoutData instanceof GridData) {
				((GridData) controlslayoutData).exclude = !visible;
			}
		}
	}

}
