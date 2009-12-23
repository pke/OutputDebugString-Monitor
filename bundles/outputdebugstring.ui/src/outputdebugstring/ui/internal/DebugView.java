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
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
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

	private volatile boolean autoscroll;
	private volatile boolean absoluteTimeFormat;
	private final DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);

	private IWorkbenchSiteProgressService progressService;

	private UIJob job;

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
		final Table table = this.viewer.getTable();
		TableViewerColumn column = new TableViewerColumn(this.viewer, SWT.LEFT);
		column.getColumn().setText("#"); //$NON-NLS-1$
		column.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				// TODO: Make this quicker
				cell.setText(String.valueOf(DebugView.this.events.indexOf(cell.getElement())));
			}
		});
		tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(20, 40));
		column = new TableViewerColumn(this.viewer, SWT.LEFT);
		column.getColumn().setText(Messages.DebugView_TimeColumnName);
		tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(20, 40));
		column.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				final DebugStringEvent event = (DebugStringEvent) cell.getElement();
				if (DebugView.this.absoluteTimeFormat) {
					cell.setText(DebugView.this.timeFormat.format(event.getDate()));
				} else {
					cell.setText(String.valueOf(event.getTimeOffset()));
				}
			}
		});
		column = new TableViewerColumn(this.viewer, SWT.LEFT);
		column.getColumn().setText(Messages.DebugView_DebugPrintolumnName);
		tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(80, 100));
		column.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				cell.setText(((DebugStringEvent) cell.getElement()).getText().replace('\n', ' '));
			}
		});
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		initToggle("outputdebugstring.ui.CaptureCommand", (Toggler) getAdapter(CaptureToggler.class)); //$NON-NLS-1$
		initToggle("outputdebugstring.ui.AutoscrollCommand", (Toggler) getAdapter(AutoScrollToggler.class)); //$NON-NLS-1$
		initToggle("outputdebugstring.ui.TimeFormatCommand", (Toggler) getAdapter(TimeFormatToggler.class)); //$NON-NLS-1$

		this.progressService = (IWorkbenchSiteProgressService) getSite()
				.getAdapter(IWorkbenchSiteProgressService.class);
	}

	private void initToggle(final String commandId, final Toggler toggler) {
		final ICommandService commandService = (ICommandService) getSite().getService(ICommandService.class);
		final Command command = commandService.getCommand(commandId);
		final State state = command.getState(RegistryToggleState.STATE_ID);
		if (state != null) {
			toggler.toggle(state.getValue().equals(true));
		}
	}

	@Override
	public void setFocus() {
		if (this.viewer != null && !this.viewer.getControl().isDisposed()) {
			this.viewer.getControl().setFocus();
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
			return new AutoScrollToggler() {
				public void toggle(final boolean toggle) {
					view.autoscroll = toggle;
					if (view.autoscroll && !view.events.isEmpty()) {
						updateViewer(new Runnable() {
							public void run() {
								final DebugStringEvent lastEvent = view.events.get(view.events.size() - 1);
								view.viewer.reveal(lastEvent);
							}
						});
					}
				}
			};
		} else if (adapter == TimeFormatToggler.class) {
			return new TimeFormatToggler() {
				public void toggle(final boolean toggle) {
					view.absoluteTimeFormat = toggle;
					updateViewer(new Runnable() {
						public void run() {
							view.viewer.refresh(true);
						}
					});
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

	private void updateControl(final Control control, final Runnable runnable) {
		if (control.isDisposed()) {
			return;
		}
		final Display display = control.getDisplay();
		if (display.getThread() == Thread.currentThread()) {
			display.syncExec(runnable);
		} else {
			display.asyncExec(new Runnable() {
				public void run() {
					if (!control.isDisposed()) {
						runnable.run();
					}
				}
			});
		}
	}

	private void updateViewer(final Runnable runnable) {
		updateControl(this.viewer.getControl(), runnable);
	}

	void refreshViewer() {
		this.progressService.schedule(getRefreshJob(), 200);
	}

	private Job getRefreshJob() {
		if (this.job == null) {
			this.job = new UIJob(this.viewer.getControl().getDisplay(), "Refreshing view") {
				@Override
				public IStatus runInUIThread(final IProgressMonitor monitor) {
					DebugView.this.viewer.refresh();
					DebugView.this.progressService.warnOfContentChange();
					if (DebugView.this.autoscroll) {
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
		updateViewer(new Runnable() {
			public void run() {
				DebugView.this.viewer.refresh();
			}
		});
	}
}
