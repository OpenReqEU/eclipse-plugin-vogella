package com.vogella.prioritizer.eclipseplugin.parts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.vogella.prioritizer.eclipseplugin.communication.CommunicationController;
import com.vogella.prioritizer.eclipseplugin.ui.BugFilter;
import com.vogella.prioritizer.eclipseplugin.ui.RankedBugViewerComparator;
import com.vogella.spring.data.entities.RankedBug;

import io.reactivex.observers.DisposableObserver;

public class PrioritizerView {

	@Inject
	private CommunicationController ctrl;

	private TableViewer tableViewer;

	private Button loadButton;
	private Label statusLabel;

	private RankedBugViewerComparator comparator;
	private BugFilter bugFilter;

	@PostConstruct
	public void createPartControl(Composite parent) {
		GridLayout parentGridLayout = new GridLayout(2, false);
		parent.setLayout(parentGridLayout);

		loadButton = new Button(parent, SWT.PUSH);
		loadButton.setText("Load Issues");
		loadButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.getTable().removeAll();
				ctrl.requestBugs(getObserver());
			}
		});
		statusLabel = new Label(parent, SWT.NONE);
		statusLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));

		Text searchText = new Text(parent, SWT.BORDER | SWT.ICON_SEARCH);
	
		GridData searchTextGridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		searchTextGridData.horizontalSpan = 2;
		searchText.setLayoutData(searchTextGridData);
		searchText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				bugFilter.setSearchText(searchText.getText());
				tableViewer.refresh();
			}
		});

		comparator = new RankedBugViewerComparator();
		bugFilter = new BugFilter();

		tableViewer = new TableViewer(parent,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setComparator(comparator);
		GridData tableViewerGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		tableViewerGridData.horizontalSpan = 2;
		tableViewer.getControl().setLayoutData(tableViewerGridData);
		tableViewer.addFilter(bugFilter);
		createColumns(tableViewer);

		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	private void createColumns(TableViewer tableViewer) {

		TableViewerColumn col = createTableViewerColumn("Bug Id", 70, 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RankedBug rankedBug = (RankedBug) element;
				return String.valueOf(rankedBug.getBugIdBugzilla());
			}
		});

		col = createTableViewerColumn("Created", 150, 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RankedBug rankedBug = (RankedBug) element;
				return rankedBug.getCreated();
			}
		});

		col = createTableViewerColumn("Last Changed", 150, 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RankedBug rankedBug = (RankedBug) element;
				return rankedBug.getLastChanged();
			}
		});

		col = createTableViewerColumn("Component", 100, 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RankedBug rankedBug = (RankedBug) element;
				return rankedBug.getComponent();
			}
		});

		col = createTableViewerColumn("Reporter", 100, 4);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RankedBug rankedBug = (RankedBug) element;
				return rankedBug.getReporter();
			}
		});

		col = createTableViewerColumn("Assigned To", 100, 5);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RankedBug rankedBug = (RankedBug) element;
				return rankedBug.getAssignedTo();
			}
		});

		col = createTableViewerColumn("Status", 100, 6);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RankedBug rankedBug = (RankedBug) element;
				return rankedBug.getStatus();
			}
		});

		col = createTableViewerColumn("Votes", 60, 7);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RankedBug rankedBug = (RankedBug) element;
				return String.valueOf(rankedBug.getVotes());
			}
		});

		col = createTableViewerColumn("CCs", 50, 8);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RankedBug rankedBug = (RankedBug) element;
				return String.valueOf(rankedBug.getCountCC());
			}
		});

		col = createTableViewerColumn("Attachments", 95, 9);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RankedBug rankedBug = (RankedBug) element;
				return String.valueOf(rankedBug.getCountAttachments());
			}
		});

		col = createTableViewerColumn("Blocks", 60, 10);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RankedBug rankedBug = (RankedBug) element;
				return String.valueOf(rankedBug.getCountBlocks());
			}
		});

		col = createTableViewerColumn("Depends On", 95, 11);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RankedBug rankedBug = (RankedBug) element;
				return String.valueOf(rankedBug.getCountDependsOn());
			}
		});

		col = createTableViewerColumn("Duplicates", 80, 12);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RankedBug rankedBug = (RankedBug) element;
				return String.valueOf(rankedBug.getCountDuplicates());
			}
		});

		col = createTableViewerColumn("Title", 200, 13);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RankedBug rankedBug = (RankedBug) element;
				return rankedBug.getTitle();
			}
		});
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		column.addSelectionListener(getSelectionAdapter(column, colNumber));
		return viewerColumn;
	}

	private SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setColumn(index);
				tableViewer.getTable().setSortDirection(comparator.getDirection());
				tableViewer.getTable().setSortColumn(column);
				tableViewer.refresh();
			}
		};
		return selectionAdapter;
	}

	@Focus
	public void setFocus() {
		loadButton.setFocus();
	}

	private DisposableObserver<java.util.List<RankedBug>> getObserver() {
		return new DisposableObserver<java.util.List<RankedBug>>() {

			java.util.List<RankedBug> bugs = new ArrayList<RankedBug>();

			@Override
			public void onComplete() {
				Display.getDefault().asyncExec(() -> {
					if (bugs.isEmpty()) {
						statusLabel.setText("No issues available");
					} else {
						SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
						statusLabel.setText("Last sync with server: " + sdf.format(new Date()));
					}
					statusLabel.requestLayout();
					tableViewer.setInput(bugs);
				});
			}

			@Override
			public void onError(Throwable e) {
				e.printStackTrace();
				Display.getDefault().asyncExec(() -> {
					statusLabel.setText("Could not connect to server");
					statusLabel.requestLayout();
				});
			}

			@Override
			public void onNext(java.util.List<RankedBug> result) {
				result.forEach(bugs::add);
			}
		};
	}
}
