package com.vogella.prioritizer.ui.parts;

import java.io.ByteArrayInputStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.vogella.prioritizer.core.service.PrioritizerService;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.swt.schedulers.SwtSchedulers;

public class PrioritizerView {

	private CompositeDisposable compositeDisposable = new CompositeDisposable();

	@Inject
	private PrioritizerService prioritizerService;

	@PostConstruct
	public void createPartControl(Composite parent) {
		ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources(), parent);

		StackLayout stackLayout = new StackLayout();
		parent.setLayout(stackLayout);

		Single<byte[]> keywordImage = prioritizerService.getKeyWordImage("simon.scholz@vogella.com", 200);

		compositeDisposable.add(keywordImage.subscribeOn(Schedulers.io())
				.observeOn(SwtSchedulers.from(parent.getDisplay())).subscribe(imageBytes -> {

					ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);

					ImageData imageData = new ImageData(byteArrayInputStream);

					ImageDescriptor imageDescriptor = ImageDescriptor.createFromImageDataProvider(zoom -> {
						return imageData;
					});
					Image image = resourceManager.createImage(imageDescriptor);

					Composite preferencesComposite = new Composite(parent, SWT.NONE);
					preferencesComposite.setLayout(new FillLayout());
					Label imgLabel = new Label(preferencesComposite, SWT.FLAT);
					imgLabel.setImage(image);

					stackLayout.topControl = preferencesComposite;
					parent.layout();
				}, err -> {
					MessageDialog.openError(parent.getShell(), "Error", err.getMessage());
				}));
	}

	@PreDestroy
	public void dispose() {
		compositeDisposable.dispose();
	}

}
