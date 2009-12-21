package outputdebugstring.monitor.internal;

import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import outputdebugstring.monitor.Listener;

/**
 * Logs debug text strings with the OSGi log service using {@link LogService#LOG_DEBUG}.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 *
 */
public class LogServiceListener implements Listener {
	private final AtomicReference<LogService> logServiceRef = new AtomicReference<LogService>();
	private ServiceReference ref;

	protected void activate(final ComponentContext context) {
		this.ref = context.getServiceReference();
	}

	protected void bind(final LogService log) {
		this.logServiceRef.set(log);
	}

	public void onDebugString(final int processId, final String text) {
		final LogService logService = this.logServiceRef.get();
		if (logService != null) {
			logService.log(this.ref, LogService.LOG_DEBUG, String.format("[%d] %s", processId, text)); //$NON-NLS-1$
		}
	}
}
