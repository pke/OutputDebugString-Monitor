package outputdebugstring.ui.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.framework.FrameworkUtil;

import outputdebugstring.ui.internal.DebugView;
import outputdebugstring.ui.internal.Toggler;

public class DebugViewToggleHandler extends AbstractHandler implements IExecutableExtension {

	private Class<?> toggleAdapterClass;

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final boolean oldValue = HandlerUtil.toggleCommandState(event.getCommand());

		final IViewPart view = HandlerUtil.getActiveSite(event).getPage().findView(DebugView.ID);
		if (view != null) {
			final Toggler toggler = (Toggler) view.getAdapter(this.toggleAdapterClass);
			if (toggler != null) {
				toggler.toggle(!oldValue);
			}
		}
		return null;
	}

	public void setInitializationData(final IConfigurationElement config, final String propertyName, final Object data)
			throws CoreException {
		try {
			this.toggleAdapterClass = FrameworkUtil.getBundle(getClass()).loadClass(data.toString());
		} catch (final ClassNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR, config.getContributor().getName(),
					"Could not create class " + data.toString(), e)); //$NON-NLS-1$
		}
	}
}
