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
package outputdebugstring.core.osgi.component;

import outputdebugstring.core.DebugStringEvent;

/**
 * Listener used by the <code>MonitorComponent</code> to report debug string events.
 *
 * <p>
 * OSGi services should implement and publish this interface to be notified by 
 * the <code>MonitorComponent</code> about new events.
 * 
 */
public interface Listener {
	/**
	 * Called when a new debug string event occurs.
	 *
	 * @param event
	 */
	void onDebugString(DebugStringEvent event);
}