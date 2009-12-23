package outputdebugstring.ui.internal;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.utils.sync.JFaceSync;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.RegistryToggleState;
import org.eclipse.ui.model.WorkbenchPartLabelProvider;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import outputdebugstring.core.DebugStringEvent;
import outputdebugstring.core.osgi.component.Listener;
import outputdebugstring.ui.Clearable;

public class DebugView extends ViewPart implements Listener, Clearable {
	public static final String ID = "outputdebugstring.ui.DebugView"; //$NON-NLS-1$

	private TableViewer viewer;
	private ServiceRegistration serviceRegistration;
	private final List<DebugStringEvent> events = new ArrayList<DebugStringEvent>();

	private State autoscroll;
	private State absoluteTimeFormat;
	private final DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);

	private IWorkbenchSiteProgressService progressService;

	private UIJob job;

	private AutoScrollToggler autoScrollToggler;

	private Table table;

	@Override
	public void createPartControl(final Composite parent) {
		final Composite tableClient = new Composite(parent, SWT.NONE);
		final TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableClient.setLayout(tableColumnLayout);
		this.viewer = new TableViewer(tableClient, SWT.FULL_SELECTION);
		this.viewer.setContentProvider(ArrayContentProvider.getInstance());
		this.viewer.setLabelProvider(new WorkbenchPartLabelProvider());
		this.viewer.setUseHashlookup(true);
		this.viewer.setInput(this.events);
		this.table = this.viewer.getTable();
		TableViewerColumn viewerColumn = new TableViewerColumn(this.viewer, SWT.LEFT);
		TableColumn column = viewerColumn.getColumn();
		tableColumnLayout.setColumnData(column, new ColumnWeightData(20, 40));
		column.setText("#"); //$NON-NLS-1$
		viewerColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				// TODO: Make this quicker
				cell.setText(String.valueOf(DebugView.this.events.indexOf(cell.getElement())));
			}
		});
		viewerColumn = new TableViewerColumn(this.viewer, SWT.LEFT);
		column = viewerColumn.getColumn();
		column.setText(Messages.DebugView_TimeColumnName);
		tableColumnLayout.setColumnData(column, new ColumnWeightData(20, 40));
		viewerColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				final DebugStringEvent event = (DebugStringEvent) cell.getElement();
				if (DebugView.this.absoluteTimeFormat.getValue().equals(true)) {
					cell.setText(DebugView.this.timeFormat.format(event.getDate()));
				} else {
					cell.setText(String.valueOf(event.getTimeOffset()));
				}
			}
		});
		viewerColumn = new TableViewerColumn(this.viewer, SWT.LEFT);
		column = viewerColumn.getColumn();
		column.setText(Messages.DebugView_DebugPrintolumnName);
		tableColumnLayout.setColumnData(column, new ColumnWeightData(80, 100));
		viewerColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				cell.setText(((DebugStringEvent) cell.getElement()).getText().replace('\n', ' '));
			}
		});
		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);

		initToggle("outputdebugstring.ui.CaptureCommand", (Toggler) getAdapter(CaptureToggler.class)); //$NON-NLS-1$
		this.autoscroll = initToggle(
				"outputdebugstring.ui.AutoscrollCommand", (Toggler) getAdapter(AutoScrollToggler.class)); //$NON-NLS-1$
		this.absoluteTimeFormat = initToggle(
				"outputdebugstring.ui.TimeFormatCommand", (Toggler) getAdapter(TimeFormatToggler.class)); //$NON-NLS-1$
		this.progressService = (IWorkbenchSiteProgressService) getSite()
				.getAdapter(IWorkbenchSiteProgressService.class);
	}

	private State initToggle(final String commandId, final Toggler toggler) {
		final ICommandService commandService = (ICommandService) getSite().getService(ICommandService.class);
		final Command command = commandService.getCommand(commandId);
		final State state = command.getState(RegistryToggleState.STATE_ID);
		if (state != null) {
			toggler.toggle(state.getValue().equals(true));
		}
		return state;
	}

	@Override
	public void setFocus() {
		if (this.table != null && !this.table.isDisposed()) {
			this.table.setFocus();
		}
	}

	@Override
	public void dispose() {
		if (this.serviceRegistration != null) {
			this.serviceRegistration.unregister();
		}

		super.dispose();
	}

	public void onDebugString(final DebugStringEvent event) {
		this.events.add(event);
		refreshViewer();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(final Class adapter) {
		final DebugView view = this;
		if (adapter == AutoScrollToggler.class) {
			if (null == this.autoScrollToggler) {
				this.autoScrollToggler = new AutoScrollToggler() {
					public void toggle(final boolean toggle) {
						if (toggle && !view.events.isEmpty()) {
							JFaceSync.reveal(view.viewer, view.events.get(view.events.size() - 1));
						}
					}
				};
			}
			return this.autoScrollToggler;
		} else if (adapter == TimeFormatToggler.class) {
			return new TimeFormatToggler() {
				public void toggle(final boolean toggle) {
					JFaceSync.refresh(DebugView.this.viewer);
				}
			};
		} else if (adapter == CaptureToggler.class) {
			return new CaptureToggler() {
				public void toggle(final boolean toggle) {
					if (!toggle && view.serviceRegistration != null) {
						view.serviceRegistration.unregister();
						view.serviceRegistration = null;
					} else if (toggle && view.serviceRegistration == null) {
						view.serviceRegistration = FrameworkUtil.getBundle(getClass()).getBundleContext()
								.registerService(Listener.class.getName(), view, null);
					}
				}
			};
		}
		return super.getAdapter(adapter);
	}

	void refreshViewer() {
		this.progressService.schedule(getRefreshJob(), 200);
	}

	private Job getRefreshJob() {
		if (this.job == null) {
			this.job = new UIJob(this.table.getDisplay(), "Refreshing view") {
				@Override
				public IStatus runInUIThread(final IProgressMonitor monitor) {
					DebugView.this.viewer.refresh();
					DebugView.this.progressService.warnOfContentChange();
					if (DebugView.this.autoscroll.getValue().equals(true)) {
						final DebugStringEvent lastEvent = DebugView.this.events.get(DebugView.this.events.size() - 1);
						DebugView.this.viewer.reveal(lastEvent);
					}
					return Status.OK_STATUS;
				}
			};
		}
		return this.job;
	}

	public void clear() {
		this.events.clear();
		JFaceSync.refresh(this.viewer);
	}
}
