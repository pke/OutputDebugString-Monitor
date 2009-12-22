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

import java.util.ArrayList;
import java.util.List;

import outputdebugstring.monitor.DebugStringEvent;
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
	private final List<Listener> listeners = new ArrayList<Listener>();

	synchronized public void addListener(final Listener listener) {
		this.listeners.add(listener);
	}

	synchronized public void removeListener(final Listener listener) {
		this.listeners.remove(listener);
	}

	protected void activate() {
		this.monitor = new Monitor() {
			@Override
			protected void onDebugString(final int processId, final String text) {
				final DebugStringEvent event = new DebugStringEvent(this, processId, text);
				synchronized (MonitorComponent.this.listeners) {
					for (final Listener listener : MonitorComponent.this.listeners) {
						try {
							listener.onDebugString(event);
						} catch (final Throwable e) {
							e.printStackTrace();
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
