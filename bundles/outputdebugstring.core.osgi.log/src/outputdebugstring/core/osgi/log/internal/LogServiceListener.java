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
package outputdebugstring.core.osgi.log.internal;

import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import outputdebugstring.core.DebugStringEvent;
import outputdebugstring.core.osgi.component.Listener;

/**
 * Logs debug text strings with the OSGi log service using {@link LogService#LOG_DEBUG}.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 *
 */
public class LogServiceListener implements Listener {
	private LogService logService;
	private ServiceReference ref;
	private final boolean logProcessName = true;
	private final String logFormat = "[%d] %s";

	protected void activate(final ComponentContext context) {
		this.ref = context.getServiceReference();
		this.logService = (LogService) context.locateService("LogService");
	}

	public void onDebugString(final DebugStringEvent event) {
		this.logService.log(this.ref, LogService.LOG_DEBUG, String.format(
				"[%d, %s] %s", event.getProcessId(), event.getProcessName(), event.getText())); //$NON-NLS-1$
	}

	protected void updated(final Map<String, Object> config) {

	}
}
