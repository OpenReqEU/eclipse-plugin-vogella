package com.vogella.prioritizer.eclipseplugin.ui.parts;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

public class PrioritizerView implements IUpdateView {

	@Inject
	private CommunicationController ctrl;

	private TableViewer tableViewer;

	private Label statusMessage;

	public void refresh() {
		tableViewer.getTable().removeAll();
		ctrl.requestUiUpdate(this);
	}

	@PostConstruct
	public void createPartControl(Composite parent) {
		GridLayout parentGridLayout = new GridLayout(3, false);
		Button tempButton = new Button(parent, SWT.PUSH);
		tempButton.setText("load");
		tempButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}
		});
		parent.setLayout(parentGridLayout);

		Label statusText = new Label(parent, SWT.NONE);
		statusText.setText("Status:");

		statusMessage = new Label(parent, SWT.NONE);
		statusMessage.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		statusMessage.setText("---");

		Text searchText = new Text(parent, SWT.BORDER | SWT.SEARCH | SWT.NO_SCROLL);

		GridData searchTextGridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		searchTextGridData.horizontalSpan = 3;
		searchText.setLayoutData(searchTextGridData);
		searchText.addModifyListener(e -> {
			ViewerFilter[] filters = tableViewer.getFilters();
			((BugFilter) filters[0]).setSearchText(searchText.getText());
			tableViewer.refresh();
		});

		tableViewer = new TableViewer(parent,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setComparator(new RankedBugViewerComparator());
		GridData tableViewerGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		tableViewerGridData.horizontalSpan = 3;
		tableViewer.getControl().setLayoutData(tableViewerGridData);
		tableViewer.addFilter(new BugFilter());
		createColumns(tableViewer);

		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
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
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RankedBugViewerComparator comparator = (RankedBugViewerComparator) tableViewer.getComparator();
				comparator.setColumn(index);
				tableViewer.getTable().setSortDirection(comparator.getDirection());
				tableViewer.getTable().setSortColumn(column);
				tableViewer.refresh();
			}
		};
	}

	@Focus
	public void setFocus() {
		tableViewer.getTable().setFocus();
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

	@Override
	public void updateView(List<RankedBug> bugs) {
		Display.getDefault().asyncExec(() -> {
			if (bugs.isEmpty()) {
				statusMessage.setText("No issues available");
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
				statusMessage.setText("Last sync with server: " + sdf.format(new Date()));
			}
			statusMessage.requestLayout();
			tableViewer.setInput(bugs);
		});
	}

	@Override
	public void setError() {
		Display.getDefault().asyncExec(() -> {
			statusMessage.setText("Could not connect to server");
			statusMessage.requestLayout();
		});
	}
}
