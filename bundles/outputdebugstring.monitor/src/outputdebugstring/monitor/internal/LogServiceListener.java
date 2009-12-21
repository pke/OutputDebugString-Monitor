package outputdebugstring.monitor.internal;

import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import outputdebugstring.monitor.Kernel32;
import outputdebugstring.monitor.Listener;

import com.sun.jna.examples.win32.W32API.HANDLE;

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
			final HANDLE process = Kernel32.INSTANCE.OpenProcess(Kernel32.PROCESS_VM_READ
					| Kernel32.PROCESS_QUERY_INFORMATION, false, processId);
			String module = "unknown"; //$NON-NLS-1$
			if (process != null) {
				try {
					final char filename[] = new char[260];
					final int result = Psapi.INSTANCE.GetModuleFileNameEx(process, null, filename, filename.length);
					if (result > 0) {
						module = String.valueOf(filename, 0, result);
					}
				} finally {
					Kernel32.INSTANCE.CloseHandle(process);
				}
			}
			logService.log(this.ref, LogService.LOG_DEBUG, String.format("[%d, %s] %s", processId, module, text)); //$NON-NLS-1$
		}
	}
}
