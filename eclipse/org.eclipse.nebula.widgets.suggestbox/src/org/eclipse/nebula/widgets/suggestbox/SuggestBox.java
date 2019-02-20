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
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.jface.layout.RowDataFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * This widget contains a {@link Text} widget and has the possibility to add
 * {@link SuggestBoxEntry} objects, which are rendered before the {@link Text}
 * widget.
 * 
 * @author Simon Scholz
 *
 * @param <T> types of the elements, which are selected in this SuggestBox.
 */
public class SuggestBox<T> extends Composite {

	public static final int INFINITE_SUGGESTBOXENTRY_AMOUNT = -1;

	private ArrayList<Consumer<SuggestBoxEntry<T>>> addedBoxesListener = new ArrayList<>();

	private ArrayList<Consumer<SuggestBoxEntry<T>>> removedBoxesListener = new ArrayList<>();

	private ResourceManager resourceManager;

	private int maxElements = INFINITE_SUGGESTBOXENTRY_AMOUNT;

	private Composite boxComposite;
	private Text text;

	private List<SuggestBoxEntry<T>> suggestBoxEntries;

	public SuggestBox(Composite parent, int style) {
		super(parent, style | SWT.BORDER);
		resourceManager = new LocalResourceManager(JFaceResources.getResources(), this);
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
		return getSuggestBoxEntries().stream().map(SuggestBoxEntry::getInput).collect(Collectors.toList());
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
	 * {@link Text} control will be hidden from the layout, as typing text in order
	 * to get proposals is not allowed then.
	 * </p>
	 * 
	 * @param maxElements maximum amount of {@link SuggestBoxEntry} objects or
	 *                    {@link SuggestBox#INFINITE_SUGGESTBOXENTRY_AMOUNT}
	 */
	public void setMaxElements(int maxElements) {
		this.maxElements = maxElements;
		if (maxElements != INFINITE_SUGGESTBOXENTRY_AMOUNT && maxElements < getSuggestBoxEntries().size()) {
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
					if (!suggestBoxEntries.isEmpty() && text.getCaretPosition() == 0) {
						SuggestBoxEntry<T> suggestBoxEntry = suggestBoxEntries.get(suggestBoxEntries.size() - 1);
						removedBoxesListener.forEach(c -> c.accept(suggestBoxEntry));
						removeBox(suggestBoxEntries.size() - 1);
					}
				}
			}
		});
	}

	private void createWidgets() {
//		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(this);
		boxComposite = new Composite(this, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.marginTop = 2;
		rowLayout.marginBottom = 2;
		rowLayout.marginLeft = 2;
		rowLayout.marginRight = 2;
		rowLayout.spacing = LayoutConstants.getSpacing().x;
		boxComposite.setLayout(rowLayout);
		boxComposite.setVisible(false);
//		GridDataFactory.fillDefaults().exclude(true).applyTo(boxComposite);
		RowDataFactory.swtDefaults().exclude(true).applyTo(boxComposite);
		text = new Text(this, SWT.SINGLE);
		text.setFont(resourceManager.createFont(getFontDescriptor()));
		text.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		RowDataFactory.swtDefaults().hint(150, SWT.DEFAULT).applyTo(text);
//		GridDataFactory.fillDefaults().grab(true, false).applyTo(text);
		RowLayoutFactory.fillDefaults().spacing(LayoutConstants.getSpacing().x).applyTo(this);
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
	 * @param newSuggestBoxEntry {@link SuggestBoxEntry}
	 */
	public void addBox(SuggestBoxEntry<T> newSuggestBoxEntry) {
		if (getMaxElements() == INFINITE_SUGGESTBOXENTRY_AMOUNT || getSuggestBoxEntries().size() < getMaxElements()) {
			suggestBoxEntries.add(newSuggestBoxEntry);
			checkTextVisible();
			newSuggestBoxEntry.create(boxComposite);
			newSuggestBoxEntry.addListener(SWT.Dispose, (suggestBoxEntry, event) -> {
				removeBox(suggestBoxEntry);
				if (suggestBoxEntries.isEmpty()) {
					changeVisible(boxComposite, false);
				}
				SuggestBox.this.layout();
			});
			if (newSuggestBoxEntry instanceof ClosableSuggestBoxEntry) {
				@SuppressWarnings("unchecked")
				ClosableSuggestBoxEntry<T> closableBoxEntry = (ClosableSuggestBoxEntry<T>) newSuggestBoxEntry;
				closableBoxEntry.addCloseClickListener((suggestBoxEntry, event) -> {
					removeBox(suggestBoxEntry);
					removedBoxesListener.forEach(c -> c.accept(suggestBoxEntry));
				});
			}
			changeVisible(boxComposite, true);
			boxComposite.pack();
			addedBoxesListener.forEach(consumer -> consumer.accept(newSuggestBoxEntry));
			getParent().layout();
		}
	}

	/**
	 * remove an entry from this widget.
	 * 
	 * @param suggestBoxEntry {@link SuggestBoxEntry}
	 */
	public void removeBox(SuggestBoxEntry<T> suggestBoxEntry) {
		suggestBoxEntries.remove(suggestBoxEntry);
		suggestBoxEntry.dispose();
		checkTextVisible();
		SuggestBox.this.layout();
	}

	/**
	 * Remove an entry at a given index.
	 * 
	 * @param index of the {@link SuggestBoxEntry}
	 */
	public void removeBox(int index) {
		SuggestBoxEntry<T> remove = suggestBoxEntries.remove(index);
		remove.dispose();
		checkTextVisible();
		SuggestBox.this.layout();
	}

	public void addSuggestBoxEntryAddedListener(Consumer<SuggestBoxEntry<T>> addedBoxListener) {
		addedBoxesListener.add(addedBoxListener);
	}

	public void removeSuggestBoxEntryAddedListener(Consumer<SuggestBoxEntry<T>> addedBoxListener) {
		addedBoxesListener.remove(addedBoxListener);
	}

	public void addSuggestBoxEntryRemovedListener(Consumer<SuggestBoxEntry<T>> addedBoxListener) {
		removedBoxesListener.add(addedBoxListener);
	}

	public void removeSuggestBoxEntryRemovedListener(Consumer<SuggestBoxEntry<T>> addedBoxListener) {
		removedBoxesListener.remove(addedBoxListener);
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
