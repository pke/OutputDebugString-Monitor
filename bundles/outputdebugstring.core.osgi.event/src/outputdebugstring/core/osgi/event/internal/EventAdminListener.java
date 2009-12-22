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
package outputdebugstring.core.osgi.event.internal;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import outputdebugstring.core.DebugStringEvent;
import outputdebugstring.core.osgi.component.Listener;

/**
 * Listener that will create an Event with the <code>"outputdebugstring/monitor"</code> topic.
 * 
 * <p>
 * The event has 2 properties:
 * <ul>
 * <li>process.id - The id of the process that created the debug string
 * <li>text - the actual debug string
 * </ul>
 * </p>
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 *
 */
public class EventAdminListener implements Listener {
	private static final String EVENT_TOPIC = "outputdebugstring/monitor"; //$NON-NLS-1$
	private static final String TEXT2 = "text"; //$NON-NLS-1$
	private static final String PROCESS_ID = "process.id"; //$NON-NLS-1$
	private EventAdmin eventAdmin;

	protected void activate(final ComponentContext context) {
		this.eventAdmin = (EventAdmin) context.locateService("EventAdmin"); //$NON-NLS-1$
	}

	public void onDebugString(final DebugStringEvent event) {
		final Map<String, Object> props = new HashMap<String, Object>();
		props.put(PROCESS_ID, event.getProcessId());
		props.put(TEXT2, event.getText());
		this.eventAdmin.postEvent(new Event(EVENT_TOPIC, props));
	}
}
