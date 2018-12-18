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
package org.eclipse.nebula.widgets.suggestbox.canvas;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * <p>
 * This widget is used to render a certain input object with an image and text,
 * which is provided by an {@link ILabelProvider}.
 * </p>
 * <p>
 * It is painted canvas, which draws a rectangle with a border and it contains,
 * the image and text of the given {@link ILabelProvider} and a &lt;x&gt; close
 * button at the end.
 * </p>
 * 
 * <pre>
 * |-------------------|
 * |&lt;image&gt; &lt;text&gt; &lt;x&gt; |
 * |-------------------|
 * </pre>
 * 
 * @author Simon Scholz
 *
 */
public abstract class SuggestBoxCanvas extends Canvas {

	private Point textExtent;
	private Rectangle rectangle;
	private Point relativePosition;

	private ILabelProvider labelProvider;
	private Object input;

	public SuggestBoxCanvas(Composite parent, int style, Object input,
			ILabelProvider labelProvider) {
		super(parent, style);
		this.setInput(input);
		this.setLabelProvider(labelProvider);
		this.setLayoutData(new RowData());
		setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		addListener();
	}

	private void addListener() {
		addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				int avatarWidth = 0;
				Image avatar = getLabelProvider().getImage(getInput());
				if (avatar != null) {
					avatarWidth = avatar.getBounds().width + 4;
				}
				Point afterTextSize = getAfterTextSize(gc);

				textExtent = gc.textExtent(getLabelProvider().getText(
						getInput()));
				gc.setBackground(getDisplay().getSystemColor(
						SWT.COLOR_WIDGET_LIGHT_SHADOW));
				setRectangle(new Rectangle(0, 0, avatarWidth + textExtent.x
						+ afterTextSize.x + 4, textExtent.y + 2));
				gc.fillRectangle(getRectangle());
				gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_GRAY));
				gc.drawRectangle(getRectangle());
				if (avatar != null) {
					gc.drawImage(avatar, 2, 2);
				}
				gc.drawText(getLabelProvider().getText(getInput()),
						avatarWidth + 2, 2, true);

				relativePosition = new Point(avatarWidth + textExtent.x + 4, 2);
				drawAfterText(gc, getRelativePosition());
				getParent().getParent().layout();
				gc.dispose();
			}

		});
	}

	protected abstract Point getAfterTextSize(GC gc);

	protected abstract void drawAfterText(GC gc, Point position);

	@Override
	public Point computeSize(int wHint, int hHint) {
		return new Point(getRectangle().width, getRectangle().height);
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		if (getRectangle() != null) {
			return new Point(getRectangle().width + 2, Math.max(22,
					getRectangle().height));
		} else {
			return new Point(64, 22);
		}
	}

	@Override
	public void dispose() {
		getLabelProvider().dispose();
		super.dispose();
	}

	public Object getInput() {
		return input;
	}

	public void setInput(Object input) {
		this.input = input;
	}

	public ILabelProvider getLabelProvider() {
		return labelProvider;
	}

	public void setLabelProvider(ILabelProvider labelProvider) {
		this.labelProvider = labelProvider;
	}

	public Rectangle getRectangle() {
		return rectangle;
	}

	public void setRectangle(Rectangle rectangle) {
		this.rectangle = rectangle;
	}

	protected final Point getRelativePosition() {
		return relativePosition;
	}
}
