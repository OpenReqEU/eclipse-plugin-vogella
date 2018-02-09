package com.vogella.prioritizer.ui.parts;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionModel;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultRowSelectionLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.vogella.prioritizer.core.service.PrioritizerService;
import com.vogella.prioritizer.core.service.model.Bug;
import com.vogella.prioritizer.ui.nattable.BugColumnPropertyAccessor;
import com.vogella.prioritizer.ui.nattable.BugHeaderDataProvider;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.swt.schedulers.SwtSchedulers;

public class PrioritizerView {

	private CompositeDisposable compositeDisposable = new CompositeDisposable();

	@Inject
	private PrioritizerService prioritizerService;

	private StackLayout stackLayout;

	@PostConstruct
	public void createPartControl(Composite parent) {
		ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources(), parent);

		stackLayout = new StackLayout();
		parent.setLayout(stackLayout);

		Composite mainComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(mainComposite);

		stackLayout.topControl = mainComposite;

		createNatTable(mainComposite);

		createSettings(parent, resourceManager);

	}

	private void createNatTable(Composite parent) {

		EventList<Bug> eventList = new BasicEventList<>(50);

		ListDataProvider<Bug> dataProvider = new ListDataProvider<>(eventList, new BugColumnPropertyAccessor());
		DataLayer dataLayer = new DataLayer(dataProvider);
		dataLayer.setColumnPercentageSizing(true);
		dataLayer.setColumnWidthPercentageByPosition(0, 10);
		dataLayer.setColumnWidthPercentageByPosition(1, 40);
		dataLayer.setColumnWidthPercentageByPosition(2, 25);
		dataLayer.setColumnWidthPercentageByPosition(3, 25);
		SelectionLayer selectionLayer = new SelectionLayer(dataLayer);
		selectionLayer.setSelectionModel(new RowSelectionModel<>(selectionLayer, dataProvider, b -> b.getId()));
		selectionLayer.addConfiguration(new DefaultRowSelectionLayerConfiguration());

		ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);

		IDataProvider headerDataProvider = new BugHeaderDataProvider();
		DataLayer headerDataLayer = new DataLayer(headerDataProvider);
		ILayer columnHeaderLayer = new ColumnHeaderLayer(headerDataLayer, viewportLayer, selectionLayer);

		CompositeLayer compositeLayer = new CompositeLayer(1, 2);
		compositeLayer.setChildLayer(GridRegion.COLUMN_HEADER, columnHeaderLayer, 0, 0);
		compositeLayer.setChildLayer(GridRegion.BODY, viewportLayer, 0, 1);

		NatTable natTable = new NatTable(parent, compositeLayer);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);

		Single<List<Bug>> suitableBugs = prioritizerService.getSuitableBugs("simon.scholz@vogella.com", 30);

		compositeDisposable.add(suitableBugs.subscribeOn(Schedulers.io())
				.observeOn(SwtSchedulers.from(parent.getDisplay())).subscribe(bugsFromServer -> {
					System.out.println(bugsFromServer);
					eventList.addAll(bugsFromServer);
					natTable.refresh(true);
				}, err -> {
					MessageDialog.openError(parent.getShell(), "Error", err.getMessage());
				}));
	}

	private void createSettings(Composite parent, ResourceManager resourceManager) {
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

					// stackLayout.topControl = preferencesComposite;
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
