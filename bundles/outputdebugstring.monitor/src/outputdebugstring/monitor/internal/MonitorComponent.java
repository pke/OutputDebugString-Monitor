package outputdebugstring.monitor.internal;

import org.osgi.service.component.ComponentContext;

import outputdebugstring.monitor.Listener;
import outputdebugstring.monitor.Monitor;

/**
 * OSGi component facilitating the {@link Monitor}.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 *
 */
public class MonitorComponent {
	private Monitor monitor;

	protected void activate(final ComponentContext context) {
		this.monitor = new Monitor() {

			@Override
			protected void onDebugString(final int processId, final String text) {
				final Object[] listeners = context.locateServices("Listener"); //$NON-NLS-1$
				if (listeners != null) {
					for (final Object listener : listeners) {
						try {
							((Listener) listener).onDebugString(processId, text);
						} catch (final Throwable e) {
						}
					}
				}
			}

		};
	}

	protected void deactivate() {
		if (this.monitor != null) {
			this.monitor.stop();
		}
	}
}
