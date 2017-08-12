package com.vogella.prioritizer.eclipseplugin.ui.parts;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.vogella.prioritizer.eclipseplugin.communication.CommunicationController;
import com.vogella.prioritizer.eclipseplugin.ui.BugFilter;
import com.vogella.prioritizer.eclipseplugin.ui.RankedBugViewerComparator;
import com.vogella.spring.data.entities.RankedBug;

public class PrioritizerView implements IUpdateView {

	@Inject
	private CommunicationController ctrl;

	private TableViewer tableViewer;

	private Label statusMessage;

	private Link issueLink;

	public void refresh() {
		tableViewer.getTable().removeAll();
		ctrl.requestUiUpdate(this);
	}

	@PostConstruct
	public void createPartControl(Composite parent) {
		GridLayout parentGridLayout = new GridLayout(1, true);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, true);
		parent.setLayoutData(gridData);
		parent.setLayout(parentGridLayout);

		createTopBar(parent);
		createSearchBar(parent);
		createTableViewer(parent);
	}

	private void createTopBar(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout panelGridLayout = new GridLayout(3, false);
		GridData panelGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		panel.setLayoutData(panelGridData);
		panel.setLayout(panelGridLayout);

		Button tempButton = new Button(panel, SWT.PUSH);
		tempButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		tempButton.setText("Load");
		tempButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}
		});

		Composite linkPanel = new Composite(panel, SWT.NONE);
		linkPanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		linkPanel.setLayout(new GridLayout(2, false));

		Label linkLabel = new Label(linkPanel, SWT.NONE);
		linkLabel.setText("Open in Browser:");

		issueLink = new Link(linkPanel, SWT.WRAP);
		issueLink.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));
		issueLink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				try {
					PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(e.text));
				} catch (PartInitException | MalformedURLException e1) {
					e1.printStackTrace();
				}
			}
		});

		Composite statusPanel = new Composite(panel, SWT.NONE);
		statusPanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		statusPanel.setLayout(new GridLayout(2, false));

		Label statusText = new Label(statusPanel, SWT.NONE);
		statusText.setText("Status:");

		statusMessage = new Label(statusPanel, SWT.NONE);
		statusMessage.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));
		statusMessage.setText("---");
	}

	private void createSearchBar(Composite parent) {
		Text searchText = new Text(parent, SWT.BORDER | SWT.SEARCH | SWT.NO_SCROLL);
		GridData searchTextGridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		// searchTextGridData.horizontalSpan = 1;
		searchText.setLayoutData(searchTextGridData);
		searchText.addModifyListener(e -> {
			ViewerFilter[] filters = tableViewer.getFilters();
			((BugFilter) filters[0]).setSearchText(searchText.getText());
			tableViewer.refresh();
		});
	}

	private void createTableViewer(Composite parent) {
		tableViewer = new TableViewer(parent,
				SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setComparator(new RankedBugViewerComparator());
		GridData tableViewerGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		// tableViewerGridData.horizontalSpan = 1;
		tableViewer.getControl().setLayoutData(tableViewerGridData);
		tableViewer.addFilter(new BugFilter());
		tableViewer.getTable().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				IStructuredSelection structuredSelection = tableViewer.getStructuredSelection();
				Object selection = structuredSelection.getFirstElement();
				if (selection instanceof RankedBug) {
					RankedBug rankedBug = (RankedBug) selection;
					String url = String.format("<a href=\"https://bugs.eclipse.org/bugs/show_bug.cgi?id=%s\">Bug %s</a>",
							rankedBug.getBugIdBugzilla(), rankedBug.getBugIdBugzilla());
					issueLink.setText(url);
					issueLink.getParent().layout();
				}
			}
		});
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

		// col = createTableViewerColumn("Created", 150, 10);
		// col.setLabelProvider(new ColumnLabelProvider() {
		// @Override
		// public String getText(Object element) {
		// RankedBug rankedBug = (RankedBug) element;
		// return rankedBug.getCreated();
		// }
		// });

		col = createTableViewerColumn("Last Changed", 150, 20);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RankedBug rankedBug = (RankedBug) element;
				return rankedBug.getLastChanged();
			}
		});

		col = createTableViewerColumn("Component", 100, 30);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RankedBug rankedBug = (RankedBug) element;
				return rankedBug.getComponent();
			}
		});

		// col = createTableViewerColumn("Reporter", 100, 40);
		// col.setLabelProvider(new ColumnLabelProvider() {
		// @Override
		// public String getText(Object element) {
		// RankedBug rankedBug = (RankedBug) element;
		// return rankedBug.getReporter();
		// }
		// });

		col = createTableViewerColumn("Assigned To", 100, 50);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RankedBug rankedBug = (RankedBug) element;
				return rankedBug.getAssignedTo();
			}
		});

		// col = createTableViewerColumn("Status", 100, 60);
		// col.setLabelProvider(new ColumnLabelProvider() {
		// @Override
		// public String getText(Object element) {
		// RankedBug rankedBug = (RankedBug) element;
		// return rankedBug.getStatus();
		// }
		// });

		col = createTableViewerColumn("Votes", 60, 70);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RankedBug rankedBug = (RankedBug) element;
				return String.valueOf(rankedBug.getVotes());
			}
		});

		col = createTableViewerColumn("CCs", 50, 80);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RankedBug rankedBug = (RankedBug) element;
				return String.valueOf(rankedBug.getCountCC());
			}
		});

		// col = createTableViewerColumn("Attachments", 95, 90);
		// col.setLabelProvider(new ColumnLabelProvider() {
		// @Override
		// public String getText(Object element) {
		// RankedBug rankedBug = (RankedBug) element;
		// return String.valueOf(rankedBug.getCountAttachments());
		// }
		// });

		col = createTableViewerColumn("Blocks", 60, 100);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RankedBug rankedBug = (RankedBug) element;
				return String.valueOf(rankedBug.getCountBlocks());
			}
		});

		// col = createTableViewerColumn("Depends On", 95, 110);
		// col.setLabelProvider(new ColumnLabelProvider() {
		// @Override
		// public String getText(Object element) {
		// RankedBug rankedBug = (RankedBug) element;
		// return String.valueOf(rankedBug.getCountDependsOn());
		// }
		// });

		col = createTableViewerColumn("Duplicates", 80, 120);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				RankedBug rankedBug = (RankedBug) element;
				return String.valueOf(rankedBug.getCountDuplicates());
			}
		});

		col = createTableViewerColumn("Title", 200, 130);
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
				statusMessage.setText("Last sync with server was at " + sdf.format(new Date()));
			}
			statusMessage.getParent().layout();
			tableViewer.setInput(bugs);
		});
	}

	@Override
	public void setError() {
		Display.getDefault().asyncExec(() -> {
			statusMessage.setText("Could not connect to server");
			statusMessage.getParent().layout();
		});
	}
}
