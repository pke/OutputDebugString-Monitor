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
package outputdebugstring.core.osgi.component.internal;

import org.osgi.service.component.ComponentContext;

import outputdebugstring.core.DebugStringEvent;
import outputdebugstring.core.DebugStringEventListener;
import outputdebugstring.core.Monitor;

/**
 * OSGi component facilitating the {@link Monitor}.
 * 
 * <p>
 * OSGi services should implement and publish the DebugStringEventListener
 * interface to be notified by the <code>MonitorComponent</code> about new
 * events.
 * 
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
				final DebugStringEvent event = new DebugStringEvent(this,
						processId, text);
				final Object[] listeners = context.locateServices("Listener"); //$NON-NLS-1$
				for (final Object listener : listeners) {
					try {
						((DebugStringEventListener) listener)
								.onDebugStringEvent(event);
					} catch (final Throwable e) {
						e.printStackTrace();
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
