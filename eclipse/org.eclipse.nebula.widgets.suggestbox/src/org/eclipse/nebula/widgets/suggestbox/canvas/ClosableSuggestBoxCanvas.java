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

import java.util.ArrayList;
import java.util.function.Consumer;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.nebula.widgets.suggestbox.canvas.listener.HoverListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

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
public class ClosableSuggestBoxCanvas extends SuggestBoxCanvas {

	private ResourceManager resourceManager;
	private ImageDescriptor closeImgDescInactive;
	private ImageDescriptor closeImgDescActive;

	private Image closeImage;

	private ArrayList<Consumer<Event>> closeClickListener = new ArrayList<>();

	private HoverListener hoverListener;

	public ClosableSuggestBoxCanvas(Composite parent, int style, Object input, ILabelProvider labelProvider) {
		super(parent, style, input, labelProvider);
		resourceManager = new LocalResourceManager(JFaceResources.getResources(), this);
		Bundle bundle = FrameworkUtil.getBundle(getClass());

		closeImgDescActive = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/close.png"), null));
		closeImgDescInactive = ImageDescriptor.createWithFlags(closeImgDescActive, SWT.IMAGE_DISABLE);

		addListener();
	}

	protected void addListener() {
		hoverListener = new HoverListener(this::getRelativePosition);
		addListener(SWT.MouseDown, event -> {
			if (hoverListener.isHover()) {
				closeClickListener.forEach(c -> c.accept(event));
			}
		});
		addMouseMoveListener(hoverListener);
		addMouseTrackListener(hoverListener);
	}

	@Override
	protected Point getAfterTextSize(GC gc) {
		closeImage = resourceManager.createImage(hoverListener.isHover() ? closeImgDescActive : closeImgDescInactive);
		int width = closeImage.getBounds().width;
		int height = closeImage.getBounds().height;

		return new Point(width, height);
	}

	@Override
	protected void drawAfterText(GC gc, Point position) {
		closeImage = resourceManager.createImage(hoverListener.isHover() ? closeImgDescActive : closeImgDescInactive);
		gc.drawImage(closeImage, position.x, position.y);
	}

	public void addCloseClickListener(Consumer<Event> listener) {
		closeClickListener.add(listener);
	}

	public void removeCloseClickListener(Consumer<Event> listener) {
		closeClickListener.remove(listener);
	}
}
