/*******************************************************************************
* Copyright (c) 2009 Philipp Kursawe.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Philipp Kursawe - initial API and implementation
******************************************************************************/
package outputdebugstring.monitor.internal;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import outputdebugstring.monitor.DebugStringEvent;
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
	private final boolean logProcessName = true;
	private final String logFormat = "[%d] %s";

	protected void activate(final ComponentContext context) {
		this.ref = context.getServiceReference();
	}

	protected void bind(final LogService log) {
		this.logServiceRef.set(log);
	}

	public void onDebugString(final DebugStringEvent event) {
		final LogService logService = this.logServiceRef.get();
		if (logService != null) {
			logService.log(this.ref, LogService.LOG_DEBUG, String.format(
					"[%d, %s] %s", event.getProcessId(), event.getProcessName(), event.getText())); //$NON-NLS-1$
		}
	}

	protected void updated(final Map<String, Object> config) {

	}
}
